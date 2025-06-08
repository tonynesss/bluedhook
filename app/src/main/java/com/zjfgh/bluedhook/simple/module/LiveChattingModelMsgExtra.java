package com.zjfgh.bluedhook.simple.module;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class LiveChattingModelMsgExtra {
    @JSONField(name = "fan_club_level")
    private Integer fanClubLevel;
    @JSONField(name = "chat_badge_length")
    private Integer chatBadgeLength;
    @JSONField(name = "gift_model")
    private GiftModelDTO giftModel;
    @JSONField(name = "fan_club_name")
    private String fanClubName;
    @JSONField(name = "chat_badge_height")
    private Integer chatBadgeHeight;
    @JSONField(name = "fans_status")
    private Integer fansStatus;
    @JSONField(name = "in_fan_club")
    private Integer inFanClub;

    public Integer getFanClubLevel() {
        return fanClubLevel;
    }

    public void setFanClubLevel(Integer fanClubLevel) {
        this.fanClubLevel = fanClubLevel;
    }

    public Integer getChatBadgeLength() {
        return chatBadgeLength;
    }

    public void setChatBadgeLength(Integer chatBadgeLength) {
        this.chatBadgeLength = chatBadgeLength;
    }

    public GiftModelDTO getGiftModel() {
        return giftModel;
    }

    public void setGiftModel(GiftModelDTO giftModel) {
        this.giftModel = giftModel;
    }

    public String getFanClubName() {
        return fanClubName;
    }

    public void setFanClubName(String fanClubName) {
        this.fanClubName = fanClubName;
    }

    public Integer getChatBadgeHeight() {
        return chatBadgeHeight;
    }

    public void setChatBadgeHeight(Integer chatBadgeHeight) {
        this.chatBadgeHeight = chatBadgeHeight;
    }

    public Integer getFansStatus() {
        return fansStatus;
    }

    public void setFansStatus(Integer fansStatus) {
        this.fansStatus = fansStatus;
    }

    public Integer getInFanClub() {
        return inFanClub;
    }

    public void setInFanClub(Integer inFanClub) {
        this.inFanClub = inFanClub;
    }

    public static class GiftModelDTO {
        @JSONField(name = "always_show_animation")
        private Boolean alwaysShowAnimation;
        @JSONField(name = "anim_many")
        private Integer animMany;
        @JSONField(name = "avatar_frame_url")
        private String avatarFrameUrl;
        @JSONField(name = "bg_color")
        private List<?> bgColor;
        @JSONField(name = "bg_img")
        private String bgImg;
        @JSONField(name = "bonus")
        private Integer bonus;
        @JSONField(name = "cleanser")
        private Integer cleanser;
        @JSONField(name = "connection_sum")
        private Integer connectionSum;
        @JSONField(name = "consume")
        private Integer consume;
        @JSONField(name = "displayCount")
        private Integer displayCount;
        @JSONField(name = "double_num")
        private Integer doubleNum;
        @JSONField(name = "draw_status")
        private Integer drawStatus;
        @JSONField(name = "enterAnimLocal")
        private Boolean enterAnimLocal;
        @JSONField(name = "event_type")
        private Integer eventType;
        @JSONField(name = "exclusive_icon")
        private Integer exclusiveIcon;
        @JSONField(name = "expire")
        private Integer expire;
        @JSONField(name = "expire_last_cur_time")
        private Integer expireLastCurTime;
        @JSONField(name = "extra")
        private ExtraDTO extra;
        @JSONField(name = "extraModel")
        private ExtraModelDTO extraModel;
        @JSONField(name = "fanClubLevel")
        private Integer fanClubLevel;
        @JSONField(name = "fanClubName")
        private String fanClubName;
        @JSONField(name = "fanStatus")
        private Integer fanStatus;
        @JSONField(name = "gameplay_type")
        private Integer gameplayType;
        @JSONField(name = "giftId")
        private Integer giftId;
        @JSONField(name = "giftType")
        private Integer giftType;
        @JSONField(name = "inFanClub")
        private Integer inFanClub;
        @JSONField(name = "info_screen_type")
        private Integer infoScreenType;
        @JSONField(name = "info_type")
        private Integer infoType;
        @JSONField(name = "isExposure")
        private Boolean isExposure;
        @JSONField(name = "isReward")
        private Boolean isReward;
        @JSONField(name = "is_battle_goods")
        private Integer isBattleGoods;
        @JSONField(name = "is_continue")
        private Boolean isContinue;
        @JSONField(name = "is_draw_goods")
        private Boolean isDrawGoods;
        @JSONField(name = "is_fans_goods")
        private Integer isFansGoods;
        @JSONField(name = "is_guess")
        private Integer isGuess;
        @JSONField(name = "is_help_wish_list")
        private Boolean isHelpWishList;
        @JSONField(name = "is_join_ticket")
        private Integer isJoinTicket;
        @JSONField(name = "is_luck_bag")
        private Boolean isLuckBag;
        @JSONField(name = "is_opponent")
        private Boolean isOpponent;
        @JSONField(name = "is_renewal")
        private Integer isRenewal;
        @JSONField(name = "is_task")
        private Integer isTask;
        @JSONField(name = "is_unexpire")
        private Integer isUnexpire;
        @JSONField(name = "level")
        private Integer level;
        @JSONField(name = "liang_type")
        private Integer liangType;
        @JSONField(name = "link")
        private String link;
        @JSONField(name = "link_extra")
        private String linkExtra;
        @JSONField(name = "link_type")
        private Integer linkType;
        @JSONField(name = "luckyBagState")
        private Integer luckyBagState;
        @JSONField(name = "onlyPlayScreen")
        private Boolean onlyPlayScreen;
        @JSONField(name = "random_name")
        private String randomName;
        @JSONField(name = "random_static")
        private String randomStatic;
        @JSONField(name = "receiver_avatar")
        private String receiverAvatar;
        @JSONField(name = "receiver_is_hide")
        private Boolean receiverIsHide;
        @JSONField(name = "receiver_mic_order")
        private Integer receiverMicOrder;
        @JSONField(name = "receiver_name")
        private String receiverName;
        @JSONField(name = "receiver_uid")
        private Integer receiverUid;
        @JSONField(name = "recent_expire")
        private Integer recentExpire;
        @JSONField(name = "respose_this_code")
        private Integer resposeThisCode;
        @JSONField(name = "send_count")
        private Integer sendCount;
        @JSONField(name = "skin_status")
        private Integer skinStatus;
        @JSONField(name = "userId")
        private Integer userId;
        @JSONField(name = "user_store_count")
        private Integer userStoreCount;
        @JSONField(name = "vibrate_status")
        private Integer vibrateStatus;
        @JSONField(name = "actual_beans")
        private Integer actualBeans;
        @JSONField(name = "anim_code")
        private String animCode;
        @JSONField(name = "animation")
        private Integer animation;
        @JSONField(name = "arTimer")
        private Integer arTimer;
        @JSONField(name = "availability")
        private Integer availability;
        @JSONField(name = "beans")
        private Integer beans;
        @JSONField(name = "beans_count")
        private Integer beansCount;
        @JSONField(name = "beans_current_count")
        private Integer beansCurrentCount;
        @JSONField(name = "comboWaitTime")
        private Integer comboWaitTime;
        @JSONField(name = "danmu_count")
        private Integer danmuCount;
        @JSONField(name = "discount")
        private Integer discount;
        @JSONField(name = "double_hit")
        private Integer doubleHit;
        @JSONField(name = "effect_time")
        private Integer effectTime;
        @JSONField(name = "fans_level")
        private Integer fansLevel;
        @JSONField(name = "free_number")
        private Integer freeNumber;
        @JSONField(name = "goods_id")
        private String goodsId;
        @JSONField(name = "hit_batch")
        private Integer hitBatch;
        @JSONField(name = "hit_count")
        private Integer hitCount;
        @JSONField(name = "hit_id")
        private Long hitId;
        @JSONField(name = "imageType")
        private Integer imageType;
        @JSONField(name = "images_apng2")
        private String imagesApng2;
        @JSONField(name = "images_gif")
        private String imagesGif;
        @JSONField(name = "images_mp4")
        private String imagesMp4;
        @JSONField(name = "images_static")
        private String imagesStatic;
        @JSONField(name = "is_hide_expire_time")
        private Integer isHideExpireTime;
        @JSONField(name = "is_my")
        private Integer isMy;
        @JSONField(name = "is_use")
        private Integer isUse;
        @JSONField(name = "one_month_beans")
        private Integer oneMonthBeans;
        @JSONField(name = "ops")
        private Integer ops;
        @JSONField(name = "opsType")
        private Integer opsType;
        @JSONField(name = "original_beans")
        private Integer originalBeans;
        @JSONField(name = "resWidth")
        private Integer resWidth;
        @JSONField(name = "resource_url")
        private String resourceUrl;
        @JSONField(name = "rich_score")
        private Integer richScore;
        @JSONField(name = "sendGiftStatus")
        private Integer sendGiftStatus;
        @JSONField(name = "sendGiftStatusLoadingTime")
        private Integer sendGiftStatusLoadingTime;
        @JSONField(name = "show_info")
        private Integer showInfo;
        @JSONField(name = "to_self")
        private Integer toSelf;
        @JSONField(name = "wealth_score")
        private Integer wealthScore;
        @JSONField(name = "count")
        private Integer count;
        @JSONField(name = "isBagGift")
        private Boolean isBagGift;
        @JSONField(name = "isMp4")
        private Boolean isMp4;
        @JSONField(name = "isSelected")
        private Boolean isSelected;
        @JSONField(name = "name")
        private String name;
        @JSONField(name = "packageTabIndex")
        private Integer packageTabIndex;
        @JSONField(name = "pageIndex")
        private Integer pageIndex;
        @JSONField(name = "positionInPage")
        private Integer positionInPage;

        public Boolean getAlwaysShowAnimation() {
            return alwaysShowAnimation;
        }

        public void setAlwaysShowAnimation(Boolean alwaysShowAnimation) {
            this.alwaysShowAnimation = alwaysShowAnimation;
        }

        public Integer getAnimMany() {
            return animMany;
        }

        public void setAnimMany(Integer animMany) {
            this.animMany = animMany;
        }

        public String getAvatarFrameUrl() {
            return avatarFrameUrl;
        }

        public void setAvatarFrameUrl(String avatarFrameUrl) {
            this.avatarFrameUrl = avatarFrameUrl;
        }

        public List<?> getBgColor() {
            return bgColor;
        }

        public void setBgColor(List<?> bgColor) {
            this.bgColor = bgColor;
        }

        public String getBgImg() {
            return bgImg;
        }

        public void setBgImg(String bgImg) {
            this.bgImg = bgImg;
        }

        public Integer getBonus() {
            return bonus;
        }

        public void setBonus(Integer bonus) {
            this.bonus = bonus;
        }

        public Integer getCleanser() {
            return cleanser;
        }

        public void setCleanser(Integer cleanser) {
            this.cleanser = cleanser;
        }

        public Integer getConnectionSum() {
            return connectionSum;
        }

        public void setConnectionSum(Integer connectionSum) {
            this.connectionSum = connectionSum;
        }

        public Integer getConsume() {
            return consume;
        }

        public void setConsume(Integer consume) {
            this.consume = consume;
        }

        public Integer getDisplayCount() {
            return displayCount;
        }

        public void setDisplayCount(Integer displayCount) {
            this.displayCount = displayCount;
        }

        public Integer getDoubleNum() {
            return doubleNum;
        }

        public void setDoubleNum(Integer doubleNum) {
            this.doubleNum = doubleNum;
        }

        public Integer getDrawStatus() {
            return drawStatus;
        }

        public void setDrawStatus(Integer drawStatus) {
            this.drawStatus = drawStatus;
        }

        public Boolean getEnterAnimLocal() {
            return enterAnimLocal;
        }

        public void setEnterAnimLocal(Boolean enterAnimLocal) {
            this.enterAnimLocal = enterAnimLocal;
        }

        public Integer getEventType() {
            return eventType;
        }

        public void setEventType(Integer eventType) {
            this.eventType = eventType;
        }

        public Integer getExclusiveIcon() {
            return exclusiveIcon;
        }

        public void setExclusiveIcon(Integer exclusiveIcon) {
            this.exclusiveIcon = exclusiveIcon;
        }

        public Integer getExpire() {
            return expire;
        }

        public void setExpire(Integer expire) {
            this.expire = expire;
        }

        public Integer getExpireLastCurTime() {
            return expireLastCurTime;
        }

        public void setExpireLastCurTime(Integer expireLastCurTime) {
            this.expireLastCurTime = expireLastCurTime;
        }

        public ExtraDTO getExtra() {
            return extra;
        }

        public void setExtra(ExtraDTO extra) {
            this.extra = extra;
        }

        public ExtraModelDTO getExtraModel() {
            return extraModel;
        }

        public void setExtraModel(ExtraModelDTO extraModel) {
            this.extraModel = extraModel;
        }

        public Integer getFanClubLevel() {
            return fanClubLevel;
        }

        public void setFanClubLevel(Integer fanClubLevel) {
            this.fanClubLevel = fanClubLevel;
        }

        public String getFanClubName() {
            return fanClubName;
        }

        public void setFanClubName(String fanClubName) {
            this.fanClubName = fanClubName;
        }

        public Integer getFanStatus() {
            return fanStatus;
        }

        public void setFanStatus(Integer fanStatus) {
            this.fanStatus = fanStatus;
        }

        public Integer getGameplayType() {
            return gameplayType;
        }

        public void setGameplayType(Integer gameplayType) {
            this.gameplayType = gameplayType;
        }

        public Integer getGiftId() {
            return giftId;
        }

        public void setGiftId(Integer giftId) {
            this.giftId = giftId;
        }

        public Integer getGiftType() {
            return giftType;
        }

        public void setGiftType(Integer giftType) {
            this.giftType = giftType;
        }

        public Integer getInFanClub() {
            return inFanClub;
        }

        public void setInFanClub(Integer inFanClub) {
            this.inFanClub = inFanClub;
        }

        public Integer getInfoScreenType() {
            return infoScreenType;
        }

        public void setInfoScreenType(Integer infoScreenType) {
            this.infoScreenType = infoScreenType;
        }

        public Integer getInfoType() {
            return infoType;
        }

        public void setInfoType(Integer infoType) {
            this.infoType = infoType;
        }

        public Boolean getIsExposure() {
            return isExposure;
        }

        public void setIsExposure(Boolean isExposure) {
            this.isExposure = isExposure;
        }

        public Boolean getIsReward() {
            return isReward;
        }

        public void setIsReward(Boolean isReward) {
            this.isReward = isReward;
        }

        public Integer getIsBattleGoods() {
            return isBattleGoods;
        }

        public void setIsBattleGoods(Integer isBattleGoods) {
            this.isBattleGoods = isBattleGoods;
        }

        public Boolean getIsContinue() {
            return isContinue;
        }

        public void setIsContinue(Boolean isContinue) {
            this.isContinue = isContinue;
        }

        public Boolean getIsDrawGoods() {
            return isDrawGoods;
        }

        public void setIsDrawGoods(Boolean isDrawGoods) {
            this.isDrawGoods = isDrawGoods;
        }

        public Integer getIsFansGoods() {
            return isFansGoods;
        }

        public void setIsFansGoods(Integer isFansGoods) {
            this.isFansGoods = isFansGoods;
        }

        public Integer getIsGuess() {
            return isGuess;
        }

        public void setIsGuess(Integer isGuess) {
            this.isGuess = isGuess;
        }

        public Boolean getIsHelpWishList() {
            return isHelpWishList;
        }

        public void setIsHelpWishList(Boolean isHelpWishList) {
            this.isHelpWishList = isHelpWishList;
        }

        public Integer getIsJoinTicket() {
            return isJoinTicket;
        }

        public void setIsJoinTicket(Integer isJoinTicket) {
            this.isJoinTicket = isJoinTicket;
        }

        public Boolean getIsLuckBag() {
            return isLuckBag;
        }

        public void setIsLuckBag(Boolean isLuckBag) {
            this.isLuckBag = isLuckBag;
        }

        public Boolean getIsOpponent() {
            return isOpponent;
        }

        public void setIsOpponent(Boolean isOpponent) {
            this.isOpponent = isOpponent;
        }

        public Integer getIsRenewal() {
            return isRenewal;
        }

        public void setIsRenewal(Integer isRenewal) {
            this.isRenewal = isRenewal;
        }

        public Integer getIsTask() {
            return isTask;
        }

        public void setIsTask(Integer isTask) {
            this.isTask = isTask;
        }

        public Integer getIsUnexpire() {
            return isUnexpire;
        }

        public void setIsUnexpire(Integer isUnexpire) {
            this.isUnexpire = isUnexpire;
        }

        public Integer getLevel() {
            return level;
        }

        public void setLevel(Integer level) {
            this.level = level;
        }

        public Integer getLiangType() {
            return liangType;
        }

        public void setLiangType(Integer liangType) {
            this.liangType = liangType;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getLinkExtra() {
            return linkExtra;
        }

        public void setLinkExtra(String linkExtra) {
            this.linkExtra = linkExtra;
        }

        public Integer getLinkType() {
            return linkType;
        }

        public void setLinkType(Integer linkType) {
            this.linkType = linkType;
        }

        public Integer getLuckyBagState() {
            return luckyBagState;
        }

        public void setLuckyBagState(Integer luckyBagState) {
            this.luckyBagState = luckyBagState;
        }

        public Boolean getOnlyPlayScreen() {
            return onlyPlayScreen;
        }

        public void setOnlyPlayScreen(Boolean onlyPlayScreen) {
            this.onlyPlayScreen = onlyPlayScreen;
        }

        public String getRandomName() {
            return randomName;
        }

        public void setRandomName(String randomName) {
            this.randomName = randomName;
        }

        public String getRandomStatic() {
            return randomStatic;
        }

        public void setRandomStatic(String randomStatic) {
            this.randomStatic = randomStatic;
        }

        public String getReceiverAvatar() {
            return receiverAvatar;
        }

        public void setReceiverAvatar(String receiverAvatar) {
            this.receiverAvatar = receiverAvatar;
        }

        public Boolean getReceiverIsHide() {
            return receiverIsHide;
        }

        public void setReceiverIsHide(Boolean receiverIsHide) {
            this.receiverIsHide = receiverIsHide;
        }

        public Integer getReceiverMicOrder() {
            return receiverMicOrder;
        }

        public void setReceiverMicOrder(Integer receiverMicOrder) {
            this.receiverMicOrder = receiverMicOrder;
        }

        public String getReceiverName() {
            return receiverName;
        }

        public void setReceiverName(String receiverName) {
            this.receiverName = receiverName;
        }

        public Integer getReceiverUid() {
            return receiverUid;
        }

        public void setReceiverUid(Integer receiverUid) {
            this.receiverUid = receiverUid;
        }

        public Integer getRecentExpire() {
            return recentExpire;
        }

        public void setRecentExpire(Integer recentExpire) {
            this.recentExpire = recentExpire;
        }

        public Integer getResposeThisCode() {
            return resposeThisCode;
        }

        public void setResposeThisCode(Integer resposeThisCode) {
            this.resposeThisCode = resposeThisCode;
        }

        public Integer getSendCount() {
            return sendCount;
        }

        public void setSendCount(Integer sendCount) {
            this.sendCount = sendCount;
        }

        public Integer getSkinStatus() {
            return skinStatus;
        }

        public void setSkinStatus(Integer skinStatus) {
            this.skinStatus = skinStatus;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public Integer getUserStoreCount() {
            return userStoreCount;
        }

        public void setUserStoreCount(Integer userStoreCount) {
            this.userStoreCount = userStoreCount;
        }

        public Integer getVibrateStatus() {
            return vibrateStatus;
        }

        public void setVibrateStatus(Integer vibrateStatus) {
            this.vibrateStatus = vibrateStatus;
        }

        public Integer getActualBeans() {
            return actualBeans;
        }

        public void setActualBeans(Integer actualBeans) {
            this.actualBeans = actualBeans;
        }

        public String getAnimCode() {
            return animCode;
        }

        public void setAnimCode(String animCode) {
            this.animCode = animCode;
        }

        public Integer getAnimation() {
            return animation;
        }

        public void setAnimation(Integer animation) {
            this.animation = animation;
        }

        public Integer getArTimer() {
            return arTimer;
        }

        public void setArTimer(Integer arTimer) {
            this.arTimer = arTimer;
        }

        public Integer getAvailability() {
            return availability;
        }

        public void setAvailability(Integer availability) {
            this.availability = availability;
        }

        public Integer getBeans() {
            return beans;
        }

        public void setBeans(Integer beans) {
            this.beans = beans;
        }

        public Integer getBeansCount() {
            return beansCount;
        }

        public void setBeansCount(Integer beansCount) {
            this.beansCount = beansCount;
        }

        public Integer getBeansCurrentCount() {
            return beansCurrentCount;
        }

        public void setBeansCurrentCount(Integer beansCurrentCount) {
            this.beansCurrentCount = beansCurrentCount;
        }

        public Integer getComboWaitTime() {
            return comboWaitTime;
        }

        public void setComboWaitTime(Integer comboWaitTime) {
            this.comboWaitTime = comboWaitTime;
        }

        public Integer getDanmuCount() {
            return danmuCount;
        }

        public void setDanmuCount(Integer danmuCount) {
            this.danmuCount = danmuCount;
        }

        public Integer getDiscount() {
            return discount;
        }

        public void setDiscount(Integer discount) {
            this.discount = discount;
        }

        public Integer getDoubleHit() {
            return doubleHit;
        }

        public void setDoubleHit(Integer doubleHit) {
            this.doubleHit = doubleHit;
        }

        public Integer getEffectTime() {
            return effectTime;
        }

        public void setEffectTime(Integer effectTime) {
            this.effectTime = effectTime;
        }

        public Integer getFansLevel() {
            return fansLevel;
        }

        public void setFansLevel(Integer fansLevel) {
            this.fansLevel = fansLevel;
        }

        public Integer getFreeNumber() {
            return freeNumber;
        }

        public void setFreeNumber(Integer freeNumber) {
            this.freeNumber = freeNumber;
        }

        public String getGoodsId() {
            return goodsId;
        }

        public void setGoodsId(String goodsId) {
            this.goodsId = goodsId;
        }

        public Integer getHitBatch() {
            return hitBatch;
        }

        public void setHitBatch(Integer hitBatch) {
            this.hitBatch = hitBatch;
        }

        public Integer getHitCount() {
            return hitCount;
        }

        public void setHitCount(Integer hitCount) {
            this.hitCount = hitCount;
        }

        public Long getHitId() {
            return hitId;
        }

        public void setHitId(Long hitId) {
            this.hitId = hitId;
        }

        public Integer getImageType() {
            return imageType;
        }

        public void setImageType(Integer imageType) {
            this.imageType = imageType;
        }

        public String getImagesApng2() {
            return imagesApng2;
        }

        public void setImagesApng2(String imagesApng2) {
            this.imagesApng2 = imagesApng2;
        }

        public String getImagesGif() {
            return imagesGif;
        }

        public void setImagesGif(String imagesGif) {
            this.imagesGif = imagesGif;
        }

        public String getImagesMp4() {
            return imagesMp4;
        }

        public void setImagesMp4(String imagesMp4) {
            this.imagesMp4 = imagesMp4;
        }

        public String getImagesStatic() {
            return imagesStatic;
        }

        public void setImagesStatic(String imagesStatic) {
            this.imagesStatic = imagesStatic;
        }

        public Integer getIsHideExpireTime() {
            return isHideExpireTime;
        }

        public void setIsHideExpireTime(Integer isHideExpireTime) {
            this.isHideExpireTime = isHideExpireTime;
        }

        public Integer getIsMy() {
            return isMy;
        }

        public void setIsMy(Integer isMy) {
            this.isMy = isMy;
        }

        public Integer getIsUse() {
            return isUse;
        }

        public void setIsUse(Integer isUse) {
            this.isUse = isUse;
        }

        public Integer getOneMonthBeans() {
            return oneMonthBeans;
        }

        public void setOneMonthBeans(Integer oneMonthBeans) {
            this.oneMonthBeans = oneMonthBeans;
        }

        public Integer getOps() {
            return ops;
        }

        public void setOps(Integer ops) {
            this.ops = ops;
        }

        public Integer getOpsType() {
            return opsType;
        }

        public void setOpsType(Integer opsType) {
            this.opsType = opsType;
        }

        public Integer getOriginalBeans() {
            return originalBeans;
        }

        public void setOriginalBeans(Integer originalBeans) {
            this.originalBeans = originalBeans;
        }

        public Integer getResWidth() {
            return resWidth;
        }

        public void setResWidth(Integer resWidth) {
            this.resWidth = resWidth;
        }

        public String getResourceUrl() {
            return resourceUrl;
        }

        public void setResourceUrl(String resourceUrl) {
            this.resourceUrl = resourceUrl;
        }

        public Integer getRichScore() {
            return richScore;
        }

        public void setRichScore(Integer richScore) {
            this.richScore = richScore;
        }

        public Integer getSendGiftStatus() {
            return sendGiftStatus;
        }

        public void setSendGiftStatus(Integer sendGiftStatus) {
            this.sendGiftStatus = sendGiftStatus;
        }

        public Integer getSendGiftStatusLoadingTime() {
            return sendGiftStatusLoadingTime;
        }

        public void setSendGiftStatusLoadingTime(Integer sendGiftStatusLoadingTime) {
            this.sendGiftStatusLoadingTime = sendGiftStatusLoadingTime;
        }

        public Integer getShowInfo() {
            return showInfo;
        }

        public void setShowInfo(Integer showInfo) {
            this.showInfo = showInfo;
        }

        public Integer getToSelf() {
            return toSelf;
        }

        public void setToSelf(Integer toSelf) {
            this.toSelf = toSelf;
        }

        public Integer getWealthScore() {
            return wealthScore;
        }

        public void setWealthScore(Integer wealthScore) {
            this.wealthScore = wealthScore;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public Boolean getIsBagGift() {
            return isBagGift;
        }

        public void setIsBagGift(Boolean isBagGift) {
            this.isBagGift = isBagGift;
        }

        public Boolean getIsMp4() {
            return isMp4;
        }

        public void setIsMp4(Boolean isMp4) {
            this.isMp4 = isMp4;
        }

        public Boolean getIsSelected() {
            return isSelected;
        }

        public void setIsSelected(Boolean isSelected) {
            this.isSelected = isSelected;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getPackageTabIndex() {
            return packageTabIndex;
        }

        public void setPackageTabIndex(Integer packageTabIndex) {
            this.packageTabIndex = packageTabIndex;
        }

        public Integer getPageIndex() {
            return pageIndex;
        }

        public void setPageIndex(Integer pageIndex) {
            this.pageIndex = pageIndex;
        }

        public Integer getPositionInPage() {
            return positionInPage;
        }

        public void setPositionInPage(Integer positionInPage) {
            this.positionInPage = positionInPage;
        }

        public static class ExtraDTO {
            @JSONField(name = "multilayer")
            private MultilayerDTO multilayer;

            public MultilayerDTO getMultilayer() {
                return multilayer;
            }

            public void setMultilayer(MultilayerDTO multilayer) {
                this.multilayer = multilayer;
            }

            public static class MultilayerDTO {
                @JSONField(name = "is_multilayer")
                private Boolean isMultilayer;
                @JSONField(name = "mp4_url")
                private String mp4Url;
                @JSONField(name = "pag_url")
                private String pagUrl;
                @JSONField(name = "uid")
                private Integer uid;

                public Boolean getIsMultilayer() {
                    return isMultilayer;
                }

                public void setIsMultilayer(Boolean isMultilayer) {
                    this.isMultilayer = isMultilayer;
                }

                public String getMp4Url() {
                    return mp4Url;
                }

                public void setMp4Url(String mp4Url) {
                    this.mp4Url = mp4Url;
                }

                public String getPagUrl() {
                    return pagUrl;
                }

                public void setPagUrl(String pagUrl) {
                    this.pagUrl = pagUrl;
                }

                public Integer getUid() {
                    return uid;
                }

                public void setUid(Integer uid) {
                    this.uid = uid;
                }
            }
        }

        public static class ExtraModelDTO {
            @JSONField(name = "chat_frame")
            private String chatFrame;
            @JSONField(name = "chat_frame_border_color")
            private List<String> chatFrameBorderColor;
            @JSONField(name = "chat_frame_frame_color")
            private List<String> chatFrameFrameColor;
            @JSONField(name = "chat_frame_gradient_type")
            private Integer chatFrameGradientType;
            @JSONField(name = "chat_frame_icon")
            private String chatFrameIcon;
            @JSONField(name = "chat_frame_icon_src")
            private Integer chatFrameIconSrc;
            @JSONField(name = "chat_frame_id")
            private Integer chatFrameId;
            @JSONField(name = "chat_frame_mode")
            private Integer chatFrameMode;
            @JSONField(name = "create_time")
            private Integer createTime;
            @JSONField(name = "days")
            private Integer days;
            @JSONField(name = "frameColors")
            private List<Integer> frameColors;
            @JSONField(name = "is_hide_expire_time")
            private Integer isHideExpireTime;
            @JSONField(name = "status")
            private Integer status;
            @JSONField(name = "stokeColors")
            private List<Integer> stokeColors;
            @JSONField(name = "update_time")
            private Integer updateTime;
            @JSONField(name = "wear")
            private Integer wear;

            public String getChatFrame() {
                return chatFrame;
            }

            public void setChatFrame(String chatFrame) {
                this.chatFrame = chatFrame;
            }

            public List<String> getChatFrameBorderColor() {
                return chatFrameBorderColor;
            }

            public void setChatFrameBorderColor(List<String> chatFrameBorderColor) {
                this.chatFrameBorderColor = chatFrameBorderColor;
            }

            public List<String> getChatFrameFrameColor() {
                return chatFrameFrameColor;
            }

            public void setChatFrameFrameColor(List<String> chatFrameFrameColor) {
                this.chatFrameFrameColor = chatFrameFrameColor;
            }

            public Integer getChatFrameGradientType() {
                return chatFrameGradientType;
            }

            public void setChatFrameGradientType(Integer chatFrameGradientType) {
                this.chatFrameGradientType = chatFrameGradientType;
            }

            public String getChatFrameIcon() {
                return chatFrameIcon;
            }

            public void setChatFrameIcon(String chatFrameIcon) {
                this.chatFrameIcon = chatFrameIcon;
            }

            public Integer getChatFrameIconSrc() {
                return chatFrameIconSrc;
            }

            public void setChatFrameIconSrc(Integer chatFrameIconSrc) {
                this.chatFrameIconSrc = chatFrameIconSrc;
            }

            public Integer getChatFrameId() {
                return chatFrameId;
            }

            public void setChatFrameId(Integer chatFrameId) {
                this.chatFrameId = chatFrameId;
            }

            public Integer getChatFrameMode() {
                return chatFrameMode;
            }

            public void setChatFrameMode(Integer chatFrameMode) {
                this.chatFrameMode = chatFrameMode;
            }

            public Integer getCreateTime() {
                return createTime;
            }

            public void setCreateTime(Integer createTime) {
                this.createTime = createTime;
            }

            public Integer getDays() {
                return days;
            }

            public void setDays(Integer days) {
                this.days = days;
            }

            public List<Integer> getFrameColors() {
                return frameColors;
            }

            public void setFrameColors(List<Integer> frameColors) {
                this.frameColors = frameColors;
            }

            public Integer getIsHideExpireTime() {
                return isHideExpireTime;
            }

            public void setIsHideExpireTime(Integer isHideExpireTime) {
                this.isHideExpireTime = isHideExpireTime;
            }

            public Integer getStatus() {
                return status;
            }

            public void setStatus(Integer status) {
                this.status = status;
            }

            public List<Integer> getStokeColors() {
                return stokeColors;
            }

            public void setStokeColors(List<Integer> stokeColors) {
                this.stokeColors = stokeColors;
            }

            public Integer getUpdateTime() {
                return updateTime;
            }

            public void setUpdateTime(Integer updateTime) {
                this.updateTime = updateTime;
            }

            public Integer getWear() {
                return wear;
            }

            public void setWear(Integer wear) {
                this.wear = wear;
            }
        }
    }
}
