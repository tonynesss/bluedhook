package com.zjfgh.bluedhook.simple.module;
import java.util.List;
import java.util.Map;

public class UserCardResponse {
    private int code;
    private String request_id;
    private long request_time;
    private long response_time;
    private List<UserProfileData> data;
    private ExtraData extra;

    // Getters and Setters
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getRequest_id() { return request_id; }
    public void setRequest_id(String request_id) { this.request_id = request_id; }
    public long getRequest_time() { return request_time; }
    public void setRequest_time(long request_time) { this.request_time = request_time; }
    public long getResponse_time() { return response_time; }
    public void setResponse_time(long response_time) { this.response_time = response_time; }
    public List<UserProfileData> getData() { return data; }
    public void setData(List<UserProfileData> data) { this.data = data; }
    public ExtraData getExtra() { return extra; }
    public void setExtra(ExtraData extra) { this.extra = extra; }
    public class UserProfileData {
        private User user;
        private Map<String, Object> great_friends;
        private List<Module> module;
        private Badge badge;
        private Map<String, Object> goods;
        private Resource resource;
        private Contract contract;
        private List<Rank> rank;
        private String enc_uid;

        // Getters and Setters
        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }
        public Map<String, Object> getGreat_friends() { return great_friends; }
        public void setGreat_friends(Map<String, Object> great_friends) { this.great_friends = great_friends; }
        public List<Module> getModule() { return module; }
        public void setModule(List<Module> module) { this.module = module; }
        public Badge getBadge() { return badge; }
        public void setBadge(Badge badge) { this.badge = badge; }
        public Map<String, Object> getGoods() { return goods; }
        public void setGoods(Map<String, Object> goods) { this.goods = goods; }
        public Resource getResource() { return resource; }
        public void setResource(Resource resource) { this.resource = resource; }
        public Contract getContract() { return contract; }
        public void setContract(Contract contract) { this.contract = contract; }
        public List<Rank> getRank() { return rank; }
        public void setRank(List<Rank> rank) { this.rank = rank; }
        public String getEnc_uid() { return enc_uid; }
        public void setEnc_uid(String enc_uid) { this.enc_uid = enc_uid; }
    }

    public class User {
        private int anchor;
        private String avatar;
        private long birthday;
        private int height;
        private String name;
        private int role;
        private long uid;
        private String union_uid;
        private int vbadge;
        private int weight;
        private int vip_grade;
        private int is_vip_annual;
        private int is_hide_distance;
        private String avatar_frame;
        private int avatar_frame_id;
        private int avatar_frame_type;
        private String chat_badge_url;
        private int chat_badge_length;
        private int chat_badge_height;
        private int age;
        private String note;
        private String location;
        private int rich_level;
        private int is_manager;
        private int vip_exp_lvl;
        private int allow_active;
        private int noble_level;
        private int relationship;
        private int can_mute;
        private int can_kick_out;
        private String v_badge_url;

        // Getters and Setters
        public int getAnchor() { return anchor; }
        public void setAnchor(int anchor) { this.anchor = anchor; }
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        public long getBirthday() { return birthday; }
        public void setBirthday(long birthday) { this.birthday = birthday; }
        public int getHeight() { return height; }
        public void setHeight(int height) { this.height = height; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getRole() { return role; }
        public void setRole(int role) { this.role = role; }
        public long getUid() { return uid; }
        public void setUid(long uid) { this.uid = uid; }
        public String getUnion_uid() { return union_uid; }
        public void setUnion_uid(String union_uid) { this.union_uid = union_uid; }
        public int getVbadge() { return vbadge; }
        public void setVbadge(int vbadge) { this.vbadge = vbadge; }
        public int getWeight() { return weight; }
        public void setWeight(int weight) { this.weight = weight; }
        public int getVip_grade() { return vip_grade; }
        public void setVip_grade(int vip_grade) { this.vip_grade = vip_grade; }
        public int getIs_vip_annual() { return is_vip_annual; }
        public void setIs_vip_annual(int is_vip_annual) { this.is_vip_annual = is_vip_annual; }
        public int getIs_hide_distance() { return is_hide_distance; }
        public void setIs_hide_distance(int is_hide_distance) { this.is_hide_distance = is_hide_distance; }
        public String getAvatar_frame() { return avatar_frame; }
        public void setAvatar_frame(String avatar_frame) { this.avatar_frame = avatar_frame; }
        public int getAvatar_frame_id() { return avatar_frame_id; }
        public void setAvatar_frame_id(int avatar_frame_id) { this.avatar_frame_id = avatar_frame_id; }
        public int getAvatar_frame_type() { return avatar_frame_type; }
        public void setAvatar_frame_type(int avatar_frame_type) { this.avatar_frame_type = avatar_frame_type; }
        public String getChat_badge_url() { return chat_badge_url; }
        public void setChat_badge_url(String chat_badge_url) { this.chat_badge_url = chat_badge_url; }
        public int getChat_badge_length() { return chat_badge_length; }
        public void setChat_badge_length(int chat_badge_length) { this.chat_badge_length = chat_badge_length; }
        public int getChat_badge_height() { return chat_badge_height; }
        public void setChat_badge_height(int chat_badge_height) { this.chat_badge_height = chat_badge_height; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public int getRich_level() { return rich_level; }
        public void setRich_level(int rich_level) { this.rich_level = rich_level; }
        public int getIs_manager() { return is_manager; }
        public void setIs_manager(int is_manager) { this.is_manager = is_manager; }
        public int getVip_exp_lvl() { return vip_exp_lvl; }
        public void setVip_exp_lvl(int vip_exp_lvl) { this.vip_exp_lvl = vip_exp_lvl; }
        public int getAllow_active() { return allow_active; }
        public void setAllow_active(int allow_active) { this.allow_active = allow_active; }
        public int getNoble_level() { return noble_level; }
        public void setNoble_level(int noble_level) { this.noble_level = noble_level; }
        public int getRelationship() { return relationship; }
        public void setRelationship(int relationship) { this.relationship = relationship; }
        public int getCan_mute() { return can_mute; }
        public void setCan_mute(int can_mute) { this.can_mute = can_mute; }
        public int getCan_kick_out() { return can_kick_out; }
        public void setCan_kick_out(int can_kick_out) { this.can_kick_out = can_kick_out; }
        public String getV_badge_url() { return v_badge_url; }
        public void setV_badge_url(String v_badge_url) { this.v_badge_url = v_badge_url; }
    }

    public class Module {
        private String desc;
        private int interaction_type;
        private String link;
        private int module_type;
        private int buried_point_type;
        private String color_image;
        private int weight;
        private String flip_desc;
        private String icon;
        private String name;
        private String color_end;
        private String color_start;
        private boolean is_max_progress;
        private int total_progress;
        private int current_progress;
        private String flip_name;

        // Getters and Setters
        public String getDesc() { return desc; }
        public void setDesc(String desc) { this.desc = desc; }
        public int getInteraction_type() { return interaction_type; }
        public void setInteraction_type(int interaction_type) { this.interaction_type = interaction_type; }
        public String getLink() { return link; }
        public void setLink(String link) { this.link = link; }
        public int getModule_type() { return module_type; }
        public void setModule_type(int module_type) { this.module_type = module_type; }
        public int getBuried_point_type() { return buried_point_type; }
        public void setBuried_point_type(int buried_point_type) { this.buried_point_type = buried_point_type; }
        public String getColor_image() { return color_image; }
        public void setColor_image(String color_image) { this.color_image = color_image; }
        public int getWeight() { return weight; }
        public void setWeight(int weight) { this.weight = weight; }
        public String getFlip_desc() { return flip_desc; }
        public void setFlip_desc(String flip_desc) { this.flip_desc = flip_desc; }
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getColor_end() { return color_end; }
        public void setColor_end(String color_end) { this.color_end = color_end; }
        public String getColor_start() { return color_start; }
        public void setColor_start(String color_start) { this.color_start = color_start; }
        public boolean isIs_max_progress() { return is_max_progress; }
        public void setIs_max_progress(boolean is_max_progress) { this.is_max_progress = is_max_progress; }
        public int getTotal_progress() { return total_progress; }
        public void setTotal_progress(int total_progress) { this.total_progress = total_progress; }
        public int getCurrent_progress() { return current_progress; }
        public void setCurrent_progress(int current_progress) { this.current_progress = current_progress; }
        public String getFlip_name() { return flip_name; }
        public void setFlip_name(String flip_name) { this.flip_name = flip_name; }
    }

    public class Badge {
        private String title;
        private String desc;
        private List<BadgeItem> data;

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDesc() { return desc; }
        public void setDesc(String desc) { this.desc = desc; }
        public List<BadgeItem> getData() { return data; }
        public void setData(List<BadgeItem> data) { this.data = data; }
    }

    public class BadgeItem {
        private String icon;

        // Getters and Setters
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
    }

    public class Resource {
        private String background;
        private String streamer;
        private String avatar_decorate;

        // Getters and Setters
        public String getBackground() { return background; }
        public void setBackground(String background) { this.background = background; }
        public String getStreamer() { return streamer; }
        public void setStreamer(String streamer) { this.streamer = streamer; }
        public String getAvatar_decorate() { return avatar_decorate; }
        public void setAvatar_decorate(String avatar_decorate) { this.avatar_decorate = avatar_decorate; }
    }

    public class Contract {
        private int count;
        private String identity_name;
        private String identity_label;
        private String identity_nickname;
        private String identity_avatar;
        private String contract_nickname;
        private String contract_avatar;
        private boolean contract_hide;
        private boolean identity_hide;
        private String contract_avatar_frame;
        private String identity_avatar_frame;
        private int contract_uid;
        private long identity_uid;
        private String link;

        // Getters and Setters
        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }
        public String getIdentity_name() { return identity_name; }
        public void setIdentity_name(String identity_name) { this.identity_name = identity_name; }
        public String getIdentity_label() { return identity_label; }
        public void setIdentity_label(String identity_label) { this.identity_label = identity_label; }
        public String getIdentity_nickname() { return identity_nickname; }
        public void setIdentity_nickname(String identity_nickname) { this.identity_nickname = identity_nickname; }
        public String getIdentity_avatar() { return identity_avatar; }
        public void setIdentity_avatar(String identity_avatar) { this.identity_avatar = identity_avatar; }
        public String getContract_nickname() { return contract_nickname; }
        public void setContract_nickname(String contract_nickname) { this.contract_nickname = contract_nickname; }
        public String getContract_avatar() { return contract_avatar; }
        public void setContract_avatar(String contract_avatar) { this.contract_avatar = contract_avatar; }
        public boolean isContract_hide() { return contract_hide; }
        public void setContract_hide(boolean contract_hide) { this.contract_hide = contract_hide; }
        public boolean isIdentity_hide() { return identity_hide; }
        public void setIdentity_hide(boolean identity_hide) { this.identity_hide = identity_hide; }
        public String getContract_avatar_frame() { return contract_avatar_frame; }
        public void setContract_avatar_frame(String contract_avatar_frame) { this.contract_avatar_frame = contract_avatar_frame; }
        public String getIdentity_avatar_frame() { return identity_avatar_frame; }
        public void setIdentity_avatar_frame(String identity_avatar_frame) { this.identity_avatar_frame = identity_avatar_frame; }
        public int getContract_uid() { return contract_uid; }
        public void setContract_uid(int contract_uid) { this.contract_uid = contract_uid; }
        public long getIdentity_uid() { return identity_uid; }
        public void setIdentity_uid(long identity_uid) { this.identity_uid = identity_uid; }
        public String getLink() { return link; }
        public void setLink(String link) { this.link = link; }
    }

    public class Rank {
        private int type;
        private String title;
        private String desc;
        private long uid;
        private String avatar;
        private String avatar_frame;
        private int hide;

        // Getters and Setters
        public int getType() { return type; }
        public void setType(int type) { this.type = type; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDesc() { return desc; }
        public void setDesc(String desc) { this.desc = desc; }
        public long getUid() { return uid; }
        public void setUid(long uid) { this.uid = uid; }
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        public String getAvatar_frame() { return avatar_frame; }
        public void setAvatar_frame(String avatar_frame) { this.avatar_frame = avatar_frame; }
        public int getHide() { return hide; }
        public void setHide(int hide) { this.hide = hide; }
    }

    public class ExtraData {
        private Behalf behalf;

        // Getters and Setters
        public Behalf getBehalf() { return behalf; }
        public void setBehalf(Behalf behalf) { this.behalf = behalf; }
    }

    public class Behalf {
        private int status;
        private String switch_enable_name;
        private String switch_disable_name;

        // Getters and Setters
        public int getStatus() { return status; }
        public void setStatus(int status) { this.status = status; }
        public String getSwitch_enable_name() { return switch_enable_name; }
        public void setSwitch_enable_name(String switch_enable_name) { this.switch_enable_name = switch_enable_name; }
        public String getSwitch_disable_name() { return switch_disable_name; }
        public void setSwitch_disable_name(String switch_disable_name) { this.switch_disable_name = switch_disable_name; }
    }
}