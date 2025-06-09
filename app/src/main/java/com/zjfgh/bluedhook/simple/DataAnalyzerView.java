package com.zjfgh.bluedhook.simple;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataAnalyzerView extends FrameLayout {
    private String lastSelectedDate = null;
    private String lastSelectedFileType = null;
    private static final int REFRESH_INTERVAL = 10000; // 10秒刷新间隔

    private JSONObject recordsData;
    private final Map<String, Map<String, List<String>>> recordsMap = new HashMap<>();
    private final List<RecordItem> currentRecords = new ArrayList<>();
    private final List<RecordItem> filteredRecords = new ArrayList<>();

    // UI组件
    private CheckBox autoAnalyzeCheckbox;
    private Spinner dateSpinner;
    private Spinner fileTypeSpinner;
    private EditText filterEditText;
    private TextView summaryTextView;
    private RecordAdapter recordAdapter;

    // 线程相关
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            refreshData();
            if (autoAnalyzeCheckbox.isChecked()) {
                mainHandler.postDelayed(this, REFRESH_INTERVAL);
            }
        }
    };

    // 礼物价值映射
    private static final Map<String, Integer> GIFT_VALUES = new HashMap<>() {{
        put("神秘人", 38);
        put("插画师", 198);
        put("医生", 688);
        put("拳击手", 2688);
        put("机长", 5688);
        put("超级影帝", 15888);
        put("猴王仙丹", 8888);
    }};
    private static final Map<String, Integer> LUCKY_GIFT_TYPE = new HashMap<>() {{
        put("幸运手镯", 4);
        put("幸运魔法棒", 12);
        put("幸运魔镜", 36);
    }};

    public DataAnalyzerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public DataAnalyzerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DataAnalyzerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(AppContainer.getInstance().getModuleRes().getLayout(R.layout.data_analyzer_view), this, true);
        // 绑定UI组件
        autoAnalyzeCheckbox = view.findViewById(R.id.auto_analyze_checkbox);
        autoAnalyzeCheckbox.setChecked(false);
        Button analyzeCheckbox = view.findViewById(R.id.analyze_checkbox);
        analyzeCheckbox.setBackground(AppContainer.getInstance().getModuleRes().getDrawable(R.drawable.button_state, null));
        analyzeCheckbox.setOnClickListener(v -> analyzeData());
        GradientDrawable getYbgButtonDrawable = new GradientDrawable();
        getYbgButtonDrawable.setCornerRadius(25f);
        dateSpinner = view.findViewById(R.id.date_spinner);
        fileTypeSpinner = view.findViewById(R.id.file_type_spinner);
        filterEditText = view.findViewById(R.id.filter_edit_text);
        summaryTextView = view.findViewById(R.id.summary_text_view);
        ViewGroup llRecyclerView = view.findViewById(R.id.ll_recycler_view);
        RecyclerView recyclerView = new RecyclerView(context);
        llRecyclerView.addView(recyclerView);
        // 设置RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recordAdapter = new RecordAdapter(new ArrayList<>());
        recyclerView.setAdapter(recordAdapter);

        // 设置监听器
        setupListeners();
        // 开始定时刷新
        mainHandler.post(refreshRunnable);
    }

    public void setRecordsData(JSONObject recordsData) {
        this.recordsData = recordsData;
        processRecordsData();
    }

    private void setupListeners() {
        // 自动分析复选框
        autoAnalyzeCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mainHandler.post(refreshRunnable);
            }
            if (isChecked && !recordsMap.isEmpty()) {
                analyzeData();
            }
        });

        // 日期选择监听
        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateFileTypeSpinner();
                if (autoAnalyzeCheckbox.isChecked()) {
                    analyzeData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 文件类型选择监听
        fileTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (autoAnalyzeCheckbox.isChecked()) {
                    analyzeData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 过滤文本监听
        filterEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRecords(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void processRecordsData() {
        executor.execute(() -> {
            try {
                recordsMap.clear();

                Iterator<String> dateKeys = recordsData.keys();
                while (dateKeys.hasNext()) {
                    String date = dateKeys.next();
                    JSONObject dateData = recordsData.getJSONObject(date);

                    Map<String, List<String>> fileTypeMap = new HashMap<>();
                    Iterator<String> fileTypeKeys = dateData.keys();
                    while (fileTypeKeys.hasNext()) {
                        String fileType = fileTypeKeys.next();
                        JSONArray recordsArray = dateData.getJSONArray(fileType);

                        List<String> recordsList = new ArrayList<>();
                        for (int i = 0; i < recordsArray.length(); i++) {
                            recordsList.add(recordsArray.getString(i));
                        }

                        fileTypeMap.put(fileType, recordsList);
                    }

                    recordsMap.put(date, fileTypeMap);
                }

                // 更新UI
                mainHandler.post(this::updateDateSpinner);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void updateDateSpinner() {
        List<String> dates = new ArrayList<>(recordsMap.keySet());

        // 保存当前选择
        String currentSelection = (String) dateSpinner.getSelectedItem();

        // 使用更安全的日期比较器
        Comparator<String> dateComparator = (d1, d2) -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                Date date1 = sdf.parse(d1);
                Date date2 = sdf.parse(d2);
                return date2.compareTo(date1); // 降序排列
            } catch (ParseException e) {
                // 确保比较是确定性的
                if (d1.equals(d2)) {
                    return 0;
                }
                return d2.compareTo(d1); // 降序排列
            }
        };

        // 使用TimSort的兼容模式
        dates.sort(dateComparator);

        ArrayAdapter<String> adapter = getStringArrayAdapter(dates);
        dateSpinner.setAdapter(adapter);

        // 恢复选择
        if (!dates.isEmpty()) {
            if (lastSelectedDate != null && dates.contains(lastSelectedDate)) {
                dateSpinner.setSelection(dates.indexOf(lastSelectedDate));
            } else if (currentSelection != null && dates.contains(currentSelection)) {
                dateSpinner.setSelection(dates.indexOf(currentSelection));
            } else {
                dateSpinner.setSelection(0);
            }
        }

        updateFileTypeSpinner();
    }

    private void updateFileTypeSpinner() {
        String selectedDate = (String) dateSpinner.getSelectedItem();
        if (selectedDate == null || !recordsMap.containsKey(selectedDate)) return;

        Map<String, List<String>> fileTypesMap = recordsMap.get(selectedDate);
        List<String> fileTypes = new ArrayList<>(fileTypesMap.keySet());

        // 保存当前选择
        String currentSelection = (String) fileTypeSpinner.getSelectedItem();

        ArrayAdapter<String> adapter = getStringArrayAdapter(fileTypes);
        fileTypeSpinner.setAdapter(adapter);

        if (!fileTypes.isEmpty()) {
            if (lastSelectedFileType != null && fileTypes.contains(lastSelectedFileType)) {
                fileTypeSpinner.setSelection(fileTypes.indexOf(lastSelectedFileType));
            } else if (currentSelection != null && fileTypes.contains(currentSelection)) {
                fileTypeSpinner.setSelection(fileTypes.indexOf(currentSelection));
            } else {
                fileTypeSpinner.setSelection(0);
            }
        }
    }

    @NonNull
    private ArrayAdapter<String> getStringArrayAdapter(List<String> dates) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,  // 默认布局（可替换）
                dates
        ) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.parseColor("#00F9FF"));  // 设置文字颜色
                textView.setBackground(AppContainer.getInstance().getModuleRes().getDrawable(R.drawable.bg_tech_space));
                return textView;
            }

            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                // 自定义下拉项样式
                TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                textView.setTextColor(Color.parseColor("#00F9FF"));  // 设置文字颜色
                textView.setBackground(AppContainer.getInstance().getModuleRes().getDrawable(R.drawable.bg_tech_space));
                return textView;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  // 确保下拉样式
        return adapter;
    }

    private void refreshData() {
        //应该调用外部方法获取新数据
        JSONObject newData = new FileToJsonConverter().convertFilesToJson();
        setRecordsData(newData);
    }

    private void analyzeData() {
        executor.execute(() -> {
            String selectedDate = (String) dateSpinner.getSelectedItem();
            String selectedFileType = (String) fileTypeSpinner.getSelectedItem();

            if (selectedDate == null || selectedFileType == null ||
                    !recordsMap.containsKey(selectedDate) ||
                    !recordsMap.get(selectedDate).containsKey(selectedFileType)) {
                return;
            }

            List<String> records = recordsMap.get(selectedDate).get(selectedFileType);
            currentRecords.clear();

            for (String record : records) {
                RecordItem item = parseRecord(record);
                if (item != null) {
                    currentRecords.add(item);
                }
            }
            Log.e("BluedHook", currentRecords.toString());
            // 按时间排序（最新的在前面）
            Collections.sort(currentRecords, (r1, r2) -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
                try {
                    Date date1 = sdf.parse(r1.time);
                    Date date2 = sdf.parse(r2.time);
                    return date2.compareTo(date1);
                } catch (ParseException e) {
                    return r2.time.compareTo(r1.time);
                }
            });

            // 更新UI
            mainHandler.post(() -> {
                filterRecords(filterEditText.getText().toString());
                updateSummary();
            });
        });
    }

    private RecordItem parseRecord(String record) {
        // 尝试解析为炼化礼物
        RecordItem item = parseGiftRecord(record);
        if (item != null) return item;

        // 尝试解析为幸运礼物
        item = parseLotteryRecord(record);
        if (item != null) return item;

        // 尝试解析为扭蛋礼物
        item = parseEggRecord(record);
        return item;
    }

    private RecordItem parseGiftRecord(String record) {
        // 正则表达式模式
        String patternGoldfire = "(\\d{4}年\\d{2}月\\d{2}日 \\d{2}:\\d{2}:\\d{2}) @\\(word:(.*?)\\) 触发金火时刻！获得 @\\(word:(.*?)\\) \\((\\d+)豆\\)x(\\d+)";
        String patternNormal = "(\\d{4}年\\d{2}月\\d{2}日 \\d{2}:\\d{2}:\\d{2}) 恭喜 @\\(word:(.*?)\\) 炼化获得 @\\(word:(.*?)\\) \\((\\d+)豆\\)x(\\d+)";
        String patternMultiple = "(\\d{4}年\\d{2}月\\d{2}日 \\d{2}:\\d{2}:\\d{2}) 恭喜 @\\(word:(.*?)\\) 触发(\\d+\\.?\\d*)倍炼化，获得 @\\(word:(.*?)\\) \\((\\d+)豆\\)x(\\d+)";

        java.util.regex.Pattern[] patterns = {
                java.util.regex.Pattern.compile(patternGoldfire),
                java.util.regex.Pattern.compile(patternNormal),
                java.util.regex.Pattern.compile(patternMultiple)
        };

        for (java.util.regex.Pattern pattern : patterns) {
            java.util.regex.Matcher matcher = pattern.matcher(record);
            if (matcher.find()) {
                String time = matcher.group(1);
                String user = matcher.group(2);
                String gift;
                int beans;
                int count;
                float multiple = 1.0f;

                if (matcher.groupCount() == 5) {
                    gift = matcher.group(3);
                    beans = Integer.parseInt(matcher.group(4));
                    count = Integer.parseInt(matcher.group(5));
                } else {
                    multiple = Float.parseFloat(matcher.group(3));
                    gift = matcher.group(4);
                    beans = Integer.parseInt(matcher.group(5));
                    count = Integer.parseInt(matcher.group(6));
                }

                int total = beans * count;
                return new RecordItem(
                        time,
                        "炼化",
                        user,
                        gift,
                        String.valueOf(beans),
                        "x" + count,
                        String.valueOf(total),
                        ""
                );
            }
        }
        return null;
    }

    private RecordItem parseLotteryRecord(String record) {
        String pattern = "(\\d{4}年\\d{2}月\\d{2}日 \\d{2}:\\d{2}:\\d{2}) 恭喜@\\(word:(\\w+)\\)触发@\\(word:(\\d+)\\)倍，获得@\\(word:(\\d+)\\)豆";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher matcher = p.matcher(record);

        if (matcher.find()) {
            String time = matcher.group(1);
            String user = matcher.group(2);
            int multiple = Integer.parseInt(Objects.requireNonNull(matcher.group(3)));
            int beans = Integer.parseInt(Objects.requireNonNull(matcher.group(4)));

            // 计算单倍豆数并尝试匹配礼物名称
            int singleBeans = beans / multiple;
            String gift = getLuckyGiftNameForValue(singleBeans);
            return new RecordItem(
                    time,
                    "幸运",
                    user,
                    gift,
                    String.valueOf(singleBeans),
                    "x" + multiple,
                    String.valueOf(beans),
                    ""
            );
        }
        return null;
    }

    private String getLuckyGiftNameForValue(int value) {
        for (Map.Entry<String, Integer> entry : LUCKY_GIFT_TYPE.entrySet()) {
            if (entry.getValue() == value) {
                return entry.getKey();
            }
        }
        return "其他幸运礼物";
    }

    private RecordItem parseEggRecord(String record) {
        String pattern = "(\\d{4}年\\d{2}月\\d{2}日 \\d{2}:\\d{2}:\\d{2}) @\\(word:([^)]+)\\) 送 @\\(word:([^)]+)\\) @\\(word:(\\d+)\\) 个 @\\(word:<扭蛋礼物>([^)]+)\\)，.*";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher matcher = p.matcher(record);
        if (matcher.find()) {
            String time = matcher.group(1);
            String user = matcher.group(2);
            String receiver = matcher.group(3);
            int count = Integer.parseInt(matcher.group(4));
            String gift = matcher.group(5).trim();
            int beans = GIFT_VALUES.getOrDefault(gift, 0);
            int total = beans;
            return new RecordItem(
                    time,
                    "扭蛋",
                    user,
                    gift,
                    String.valueOf(beans),
                    "x" + count,
                    String.valueOf(total),
                    receiver
            );
        }
        return null;
    }

    private void filterRecords(String filterText) {
        filteredRecords.clear();

        if (filterText.isEmpty()) {
            filteredRecords.addAll(currentRecords);
        } else {
            // 分割过滤条件（支持|分隔符）
            String[] filters = filterText.split("\\|");
            for (RecordItem item : currentRecords) {
                for (String filter : filters) {
                    String lowerFilter = filter.trim().toLowerCase();
                    if (item.contains(lowerFilter)) {
                        filteredRecords.add(item);
                        break;
                    }
                }
            }
        }

        recordAdapter.updateRecords(filteredRecords);
        updateSummary();
    }

    private void updateSummary() {
        if (filteredRecords.isEmpty()) {
            summaryTextView.setText("没有匹配的记录");
            return;
        }

        // 计算最大值
        int maxMultiplier = 0;
        RecordItem maxRecord = null;

        for (RecordItem item : filteredRecords) {
            try {
                int multiplier = Integer.parseInt(item.count);
                if (multiplier > maxMultiplier) {
                    maxMultiplier = multiplier;
                    maxRecord = item;
                }
            } catch (NumberFormatException ignored) {
            }
        }

        // 构建摘要文本
        if (maxRecord != null) {
            String timePart = maxRecord.time;
            String summary = String.format("近期最大出奖 %s %s 抽出 %s %d 倍 共 %s 豆。\n\n",
                    timePart, maxRecord.user, maxRecord.gift, maxMultiplier, maxRecord.total);

//            // 添加最近三条记录
//            int count = Math.min(3, filteredRecords.size());
//            for (int i = filteredRecords.size() - 1; i >= filteredRecords.size() - count; i--) {
//                RecordItem item = filteredRecords.get(i);
//                String itemTime = item.time;
//                summary += String.format(" %s %s 抽中 %s 倍 %s，获得 %s 豆。\n\n",
//                        itemTime, item.user, item.count, item.gift, item.total);
//            }

            summaryTextView.setText(summary);
        } else {
            summaryTextView.setText("没有可用的统计信息");
        }
    }

    public void onDestroy() {
        // 清理资源
        mainHandler.removeCallbacks(refreshRunnable);
        executor.shutdownNow();
    }

    // 记录项数据类
    public static class RecordItem {
        public String time;
        public String giftType;
        public String user;
        public String gift;
        public String beans;
        public String count;
        public String total;
        public String toAnchor;

        public RecordItem(String time, String giftType, String user, String gift,
                          String beans, String count, String total, String toAnchor) {
            this.time = time;
            this.giftType = giftType;
            this.user = user;
            this.gift = gift;
            this.beans = beans;
            this.count = count;
            this.total = total;
            this.toAnchor = toAnchor;
        }

        public boolean contains(String keyword) {
            return time.toLowerCase().contains(keyword) ||
                    giftType.toLowerCase().contains(keyword) ||
                    user.toLowerCase().contains(keyword) ||
                    gift.toLowerCase().contains(keyword) ||
                    beans.toLowerCase().contains(keyword) ||
                    count.toLowerCase().contains(keyword) ||
                    total.toLowerCase().contains(keyword) ||
                    (toAnchor != null && toAnchor.toLowerCase().contains(keyword));
        }
    }

    // RecyclerView适配器
    private static class RecordAdapter extends RecyclerView.Adapter<RecordViewHolder> {
        private List<RecordItem> records;

        public RecordAdapter(List<RecordItem> records) {
            this.records = records;
        }

        public void updateRecords(List<RecordItem> newRecords) {
            this.records = newRecords;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(AppContainer.getInstance().getModuleRes().getLayout(R.layout.record_item), parent, false);
            return new RecordViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
            RecordItem item = records.get(position);
            holder.bind(item);
        }

        @Override
        public int getItemCount() {
            return records.size();
        }
    }

    // ViewHolder类
    private static class RecordViewHolder extends RecyclerView.ViewHolder {
        // 这里应该定义记录项的各个TextView
        private final HorizontalScrollView scrollView;
        private final LinearLayout contentLayout;
        private final TextView timeView;
        private final TextView giftTypeView;
        private final TextView userView;
        private final TextView giftView;
        private final TextView beansView;
        private final TextView countView;
        private final TextView totalView;
        private final TextView toAnchorView;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            // 初始化各个TextView
            timeView = itemView.findViewById(R.id.time_text);
            giftTypeView = itemView.findViewById(R.id.gift_type_text);
            userView = itemView.findViewById(R.id.user_text);
            giftView = itemView.findViewById(R.id.gift_text);
            beansView = itemView.findViewById(R.id.beans_text);
            countView = itemView.findViewById(R.id.count_text);
            totalView = itemView.findViewById(R.id.total_text);
            toAnchorView = itemView.findViewById(R.id.to_anchor_text);
            scrollView = itemView.findViewById(R.id.scrollView);
            contentLayout = itemView.findViewById(R.id.contentLayout);
            // 添加全局布局监听器
//            contentLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    contentLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//
//                    // 检查内容是否超出屏幕
//                    if (contentLayout.getWidth() > scrollView.getWidth()) {
//                        // 使用Handler延迟执行滚动，确保布局已完成
//                        new Handler().postDelayed(() -> {
//                            // 平滑滚动到最右端
//                            scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
//
//                            // 可选：设置自动来回滚动动画
//                            //startAutoScrolling(scrollView, contentLayout);
//                        }, 1000); // 1秒后开始滚动
//                    }
//                }
//            });
        }

        @SuppressLint("SetTextI18n")
        public void bind(RecordItem item) {
            String[] dateTime = item.time.split(" ");
            timeView.setText(dateTime[1]);
            giftTypeView.setText(item.giftType);
            userView.setText(item.user);
            giftView.setText("抽中 " + item.gift + " 礼物");
            beansView.setText("价值 " + item.beans + " ");
            countView.setText(item.count + " ");
            totalView.setText("总计 " + item.total + " 豆");
            if (!item.toAnchor.isEmpty()) {
                toAnchorView.setText(" 赠送给 " + item.toAnchor);
            }
        }

        // 自动滚动方法
        private void startAutoScrolling(final HorizontalScrollView scrollView, final LinearLayout contentLayout) {
            final int scrollDuration = 15000; // 15秒完成一次完整滚动
            final int totalWidth = contentLayout.getWidth() - scrollView.getWidth();

            ValueAnimator animator = ValueAnimator.ofInt(0, totalWidth);
            animator.setDuration(scrollDuration);
            animator.setInterpolator(new LinearInterpolator());
            animator.addUpdateListener(animation -> {
                int scrollTo = (int) animation.getAnimatedValue();
                scrollView.scrollTo(scrollTo, 0);
            });
            // 设置无限循环
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.REVERSE);
            animator.start();
        }
    }
}
