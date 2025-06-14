package com.zjfgh.bluedhook.simple;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataAnalyzerView extends FrameLayout {
    private static final int REFRESH_INTERVAL = 10000; // 10秒刷新间隔

    private JSONObject recordsData;
    private final Map<String, Map<String, List<String>>> recordsMap = new HashMap<>();
    private final List<RecordItem> currentRecords = new CopyOnWriteArrayList<>();
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
        put("烛光", 18);
        put("花灯", 98);
        put("敦煌恋歌", 198);
        put("走进敦煌", 508);
        put("九色神鹿", 688);
        put("舞动敦煌", 2688);
        put("飞天传说", 5688);
        put("[隐藏款]一梦敦煌", 18888);
        put("神圣体魄", 18);
        put("黄金手套", 66);
        put("黄金战靴", 128);
        put("黄金头盔", 528);
        put("黄金铠甲", 1288);
        put("圣剑降临", 5088);
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
        GradientDrawable analyzeCheckboxDrawable = new GradientDrawable();
        analyzeCheckboxDrawable.setCornerRadius(25f);
        dateSpinner = view.findViewById(R.id.date_spinner);
        fileTypeSpinner = view.findViewById(R.id.file_type_spinner);
        filterEditText = view.findViewById(R.id.filter_edit_text);
        summaryTextView = view.findViewById(R.id.summary_text_view);
        summaryTextView.setBackground(AppContainer.getInstance().getModuleRes().getDrawable(R.drawable.bg_tech_tag, null));
        ViewGroup llRecyclerView = view.findViewById(R.id.ll_recycler_view);
        llRecyclerView.setBackground(AppContainer.getInstance().getModuleRes().getDrawable(R.drawable.bg_blued_rounded, null));
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
                Log.e("BluedHook", "processRecordsData->" + e);
            }
        });
    }

    private void updateDateSpinner() {
        List<String> dates = new ArrayList<>(recordsMap.keySet());

        // 保存当前选择
        String currentSelection = (String) dateSpinner.getSelectedItem();
        ArrayAdapter<String> adapter = getStringArrayAdapter(dates);
        dateSpinner.setAdapter(adapter);

        // 恢复选择
        if (!dates.isEmpty()) {
            if (currentSelection != null && dates.contains(currentSelection)) {
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
        List<String> fileTypes = null;
        if (fileTypesMap != null) {
            fileTypes = new ArrayList<>(fileTypesMap.keySet());
        }

        // 保存当前选择
        String currentSelection = (String) fileTypeSpinner.getSelectedItem();

        ArrayAdapter<String> adapter = getStringArrayAdapter(fileTypes);
        fileTypeSpinner.setAdapter(adapter);

        if (fileTypes != null && !fileTypes.isEmpty()) {
            if (currentSelection != null && fileTypes.contains(currentSelection)) {
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
                textView.setBackground(AppContainer.getInstance().getModuleRes().getDrawable(R.drawable.bg_transparent, null));
                return textView;
            }

            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                // 自定义下拉项样式
                TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                textView.setTextColor(Color.parseColor("#00F9FF"));  // 设置文字颜色
                textView.setBackground(AppContainer.getInstance().getModuleRes().getDrawable(R.drawable.bg_tech_space, null));
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
                    !Objects.requireNonNull(recordsMap.get(selectedDate)).containsKey(selectedFileType)) {
                return;
            }

            List<String> records = Objects.requireNonNull(recordsMap.get(selectedDate)).get(selectedFileType);
            currentRecords.clear();

            if (records != null) {
                for (String record : records) {
                    RecordItem item = parseRecord(record);
                    if (item != null) {
                        currentRecords.add(item);
                    }
                }
            }
            Collections.reverse(currentRecords);
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
        String patternGoldFire = "(\\d{4}年\\d{2}月\\d{2}日 \\d{2}:\\d{2}:\\d{2}(?:\\.\\d{3})?) @\\(word:(.*?)\\) 触发金火时刻！获得 @\\(word:(.*?)\\) \\((\\d+)豆\\)x(\\d+)";
        String patternNormal = "(\\d{4}年\\d{2}月\\d{2}日 \\d{2}:\\d{2}:\\d{2}(?:\\.\\d{3})?) 恭喜 @\\(word:(.*?)\\) 炼化获得 @\\(word:(.*?)\\) \\((\\d+)豆\\)x(\\d+)";
        String patternMultiple = "(\\d{4}年\\d{2}月\\d{2}日 \\d{2}:\\d{2}:\\d{2}(?:\\.\\d{3})?) 恭喜 @\\(word:(.*?)\\) 触发(\\d+\\.?\\d*)倍炼化，获得 @\\(word:(.*?)\\) \\((\\d+)豆\\)x(\\d+)";

        java.util.regex.Pattern[] patterns = {
                java.util.regex.Pattern.compile(patternGoldFire),
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
                if (matcher.groupCount() == 5) {
                    gift = matcher.group(3);
                    beans = Integer.parseInt(Objects.requireNonNull(matcher.group(4)));
                    count = Integer.parseInt(Objects.requireNonNull(matcher.group(5)));
                } else {
                    gift = matcher.group(4);
                    beans = Integer.parseInt(Objects.requireNonNull(matcher.group(5)));
                    count = Integer.parseInt(Objects.requireNonNull(matcher.group(6)));
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
        String pattern = "(\\d{4}年\\d{2}月\\d{2}日 \\d{2}:\\d{2}:\\d{2}(?:\\.\\d{3})?) 恭喜@\\(word:(\\w+)\\)触发@\\(word:(\\d+)\\)倍，获得@\\(word:(\\d+)\\)豆";
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
        String pattern = "(\\d{4}年\\d{2}月\\d{2}日 \\d{2}:\\d{2}:\\d{2}(?:\\.\\d{3})?) @\\(word:([^)]+)\\) 送 @\\(word:([^)]+)\\) @\\(word:(\\d+)\\) 个 @\\(word:<扭蛋礼物>([^)]+)\\)，.*";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher matcher = p.matcher(record);
        if (matcher.find()) {
            String time = matcher.group(1);
            String user = matcher.group(2);
            String receiver = matcher.group(3);
            int count = Integer.parseInt(Objects.requireNonNull(matcher.group(4)));
            String gift = Objects.requireNonNull(matcher.group(5)).trim();
            int beans = Optional.ofNullable(GIFT_VALUES.get(gift)).orElse(0);
            return new RecordItem(
                    time,
                    "扭蛋",
                    user,
                    gift,
                    String.valueOf(beans),
                    "x" + count,
                    String.valueOf(beans),
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
            // 先用 | 分割，处理OR条件
            String[] orConditions = filterText.split("\\|");

            for (RecordItem item : currentRecords) {
                boolean matchAnyOr = false;

                // 检查每个OR条件
                for (String orCondition : orConditions) {
                    // 用 & 分割，处理AND条件
                    String[] andConditions = orCondition.trim().split("&");
                    boolean matchAllAnd = true;

                    // 检查所有AND条件
                    for (String andCondition : andConditions) {
                        String lowerCondition = andCondition.trim().toLowerCase();
                        if (!lowerCondition.isEmpty() && !item.contains(lowerCondition)) {
                            matchAllAnd = false;
                            break;
                        }
                    }

                    // 如果满足所有AND条件，则此OR条件成立
                    if (matchAllAnd && andConditions.length > 0) {
                        matchAnyOr = true;
                        break;
                    }
                }

                if (matchAnyOr) {
                    filteredRecords.add(item);
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
                int multiplier = Integer.parseInt(item.count.split("x")[1]);
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
            @SuppressLint("DefaultLocale") String summary = String.format("近期最大出奖 %s %s 抽出 %s %d 倍 共 %s 豆。",
                    timePart, maxRecord.user, maxRecord.gift, maxMultiplier, maxRecord.total);
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
    private class RecordAdapter extends RecyclerView.Adapter<RecordViewHolder> {
        private List<RecordItem> records;

        public RecordAdapter(List<RecordItem> records) {
            this.records = records;
        }

        public void updateRecords(List<RecordItem> newRecords) {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new RecordDiffCallback(this.records, newRecords));
            this.records = new ArrayList<>(newRecords); // 创建新列表避免引用问题
            diffResult.dispatchUpdatesTo(this); // 自动计算最小更新集
        }

        // 自定义DiffCallback
        private class RecordDiffCallback extends DiffUtil.Callback {
            private final List<RecordItem> oldList;
            private final List<RecordItem> newList;

            public RecordDiffCallback(List<RecordItem> oldList, List<RecordItem> newList) {
                this.oldList = oldList;
                this.newList = newList;
            }

            @Override
            public int getOldListSize() {
                return oldList.size();
            }

            @Override
            public int getNewListSize() {
                return newList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                // 判断是否是同一个item（通常比较唯一ID）
                return Objects.equals(oldList.get(oldItemPosition).time, newList.get(newItemPosition).time);
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                // 判断内容是否相同
                return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
            }
        }

        @NonNull
        @Override
        public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(AppContainer.getInstance().getModuleRes().getLayout(R.layout.record_item), parent, false);
            return new RecordViewHolder(view, this);
        }

        @Override
        public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
            RecordItem item = records.get(position);
            holder.bind(item, position);
        }

        @Override
        public int getItemCount() {
            return records.size();
        }
    }

    private int totalBeans;

    // ViewHolder类
    private class RecordViewHolder extends RecyclerView.ViewHolder {
        // 这里应该定义记录项的各个TextView
        private final RecordAdapter adapter;
        private final View parentView;
        private final TextView timeView;
        private final TextView giftTypeView;
        private final TextView userView;
        private final TextView giftView;
        private final TextView beansView;
        private final TextView countView;
        private final TextView totalView;
        private final TextView toAnchorView;

        public RecordViewHolder(@NonNull View itemView, RecordAdapter adapter) {
            super(itemView);
            // 初始化各个TextView
            this.adapter = adapter;
            timeView = itemView.findViewById(R.id.time_text);
            giftTypeView = itemView.findViewById(R.id.gift_type_text);
            userView = itemView.findViewById(R.id.user_text);
            giftView = itemView.findViewById(R.id.gift_text);
            beansView = itemView.findViewById(R.id.beans_text);
            countView = itemView.findViewById(R.id.count_text);
            totalView = itemView.findViewById(R.id.total_text);
            toAnchorView = itemView.findViewById(R.id.to_anchor_text);
            parentView = (View) timeView.getParent();
        }

        @SuppressLint("SetTextI18n")
        public void bind(RecordItem item, int position) {
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
            //拼接字符串用于复制
            String s = timeView.getText().toString() + giftTypeView.getText()
                    + userView.getText() + giftView.getText() + beansView.getText()
                    + countView.getText() + totalView.getText() + toAnchorView.getText();
            parentView.setOnClickListener(v -> ModuleTools.copyToClipboard(AppContainer.getInstance().getBluedContext(), "飘屏内容", s));
            // 设置长按事件
            parentView.setOnLongClickListener(v -> {
                totalBeans = 0;
                for (int i = position + 1; i < adapter.getItemCount(); i++) {
                    RecordItem positionItem = adapter.records.get(i);
                    totalBeans += Integer.parseInt(positionItem.total);
                    ModuleTools.showBluedToast("该用户之前累计礼物总豆" + totalBeans);
                }
                return true;
            });
        }
    }
}
