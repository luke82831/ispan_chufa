package com.ispan.chufa.util;

public class LineUserInfo {
    private String userId; // LINE 的使用者 ID
    private String displayName; // 顯示名稱
    private String pictureUrl; // 大頭貼 URL
    private String email; // 如果請求了 email scope

    // Getter 和 Setter
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
