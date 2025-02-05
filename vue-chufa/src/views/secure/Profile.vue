<template>
  <div class="profile-container" v-if="profileLoaded">
    <!-- Header: Profile Picture and User Info -->
    <div class="profile-header">
      <img
        :src="member.profile_picture"
        alt="Profile Picture"
        class="profile-picture"
        @error="onImageError"
      />
      <div class="user-info">
        <!-- 左側文字區（會員名稱與歡迎文字）與右側操作按鈕區 -->
        <div class="info-row">
          <div class="text-info">
            <h1>{{ member.name || "會員名稱" }}</h1>
            <p class="welcome-text">歡迎回來，{{ member.nickname || "暱稱" }}！</p>
          </div>
          <div class="header-actions">
            <button class="btn btn-edit" @click="editProfile">編輯資料</button>
            <button class="btn btn-admin" v-if="isAdmin" @click="navigateToAdmin">
              管理會員資料
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Profile Details -->
    <div v-if="!isEditing" id="profile-info" class="profile-details-container">
      <dl class="profile-details">
        <div class="detail-item">
          <dt>用戶名：</dt>
          <dd>{{ member.username || "用戶名" }}</dd>
        </div>
        <div class="detail-item">
          <dt>電子郵件：</dt>
          <dd>{{ member.email || "電子郵件" }}</dd>
        </div>
        <div class="detail-item">
          <dt>手機號碼：</dt>
          <dd>{{ member.phone_number || "手機號碼" }}</dd>
        </div>
        <div class="detail-item">
          <dt>性別：</dt>
          <dd>{{ member.gender || "性別" }}</dd>
        </div>
        <div class="detail-item">
          <dt>生日：</dt>
          <dd>{{ formatDate(member.birth) || "生日" }}</dd>
        </div>
        <div class="detail-item">
          <dt>個人簡介：</dt>
          <dd>{{ member.bio || "個人簡介" }}</dd>
        </div>
      </dl>
      <!-- 顯示社群連結 -->
      <div v-if="member.socialLinks" class="social-links-section">
        <h3>社群連結</h3>
        <ul class="social-icons">
          <li v-if="member.socialLinks.facebook">
            <a
              :href="member.socialLinks.facebook"
              target="_blank"
              rel="noopener noreferrer"
              class="facebook"
            >
              <i class="fab fa-facebook-f"></i>
            </a>
          </li>
          <li v-if="member.socialLinks.instagram">
            <a
              :href="member.socialLinks.instagram"
              target="_blank"
              rel="noopener noreferrer"
              class="instagram"
            >
              <i class="fab fa-instagram"></i>
            </a>
          </li>
        </ul>
      </div>
    </div>

    <!-- Edit Form -->
    <div v-else id="edit-form" class="edit-form-container">
      <h4>編輯個人資料</h4>
      <form @submit.prevent="saveProfile">
        <div class="form-group">
          <label for="profilePicture">上傳大頭貼：</label>
          <input
            type="file"
            id="profilePicture"
            @change="uploadProfilePicture"
            class="form-control"
          />
        </div>
        <div class="form-group">
          <label for="name">姓名：</label>
          <input type="text" id="name" v-model="editMember.name" class="form-control" />
        </div>
        <div class="form-group">
          <label for="nickname">暱稱：</label>
          <input
            type="text"
            id="nickname"
            v-model="editMember.nickname"
            class="form-control"
          />
        </div>
        <div class="form-group">
          <label for="phoneNumber">手機號碼：</label>
          <input
            type="text"
            id="phoneNumber"
            v-model="editMember.phone_number"
            class="form-control"
          />
        </div>
        <div class="form-group">
          <label for="gender">性別：</label>
          <select id="gender" v-model="editMember.gender" class="form-control">
            <option value="MALE">男 (MALE)</option>
            <option value="FEMALE">女 (FEMALE)</option>
          </select>
        </div>
        <div class="form-group">
          <label for="email">電子郵件：</label>
          <input
            type="email"
            id="email"
            v-model="editMember.email"
            class="form-control"
          />
        </div>
        <div class="form-group">
          <label for="bio">個人簡介：</label>
          <textarea id="bio" v-model="editMember.bio" class="form-control"></textarea>
        </div>
        <!-- 社群連結輸入欄位 -->
        <h4>社群連結</h4>
        <div class="form-group">
          <label for="facebook">Facebook URL:</label>
          <input
            type="url"
            id="facebook"
            v-model="editMember.socialLinks.facebook"
            class="form-control"
            placeholder="https://facebook.com/yourpage"
          />
        </div>
        <div class="form-group">
          <label for="instagram">Instagram URL:</label>
          <input
            type="url"
            id="instagram"
            v-model="editMember.socialLinks.instagram"
            class="form-control"
            placeholder="https://instagram.com/yourprofile"
          />
        </div>
        <button type="submit" class="btn btn-save">儲存變更</button>
        <button type="button" class="btn btn-secondary" @click="cancelEdit">取消</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import axios from "@/plugins/axios.js";
import Swal from "sweetalert2";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/user.js";

const userStore = useUserStore();
const router = useRouter();

const isEditing = ref(false);
const profileLoaded = ref(false);

// 從 Pinia store 取得會員資料
const member = computed(() => userStore.member);
// 初始化編輯資料時，確保 socialLinks 不為 undefined
const editMember = ref({
  ...userStore.member,
  socialLinks: { ...(userStore.member.socialLinks || {}) },
});

const isAdmin = ref(false);

const formatDate = (date) => {
  if (!date) return "";
  const options = { year: "numeric", month: "2-digit", day: "2-digit" };
  return new Date(date).toLocaleDateString("zh-TW", options);
};

const fetchProfile = async () => {
  try {
    const token = localStorage.getItem("token");
    if (!token) {
      Swal.fire("錯誤", "未登入或登入已過期，請重新登入", "error");
      router.push("/secure/Login");
      return;
    }
    const response = await axios.get("/ajax/secure/profile", {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (response.data.success) {
      let memberData = { ...response.data.user };
      if (response.data.socialLinks) {
        memberData.socialLinks = response.data.socialLinks;
      }
      userStore.member = memberData;
      editMember.value = {
        ...userStore.member,
        socialLinks: { ...(userStore.member.socialLinks || {}) },
      };
      isAdmin.value = response.data.user.role === "ADMIN";
    } else {
      Swal.fire("錯誤", response.data.message, "error");
    }
    profileLoaded.value = true;
  } catch (error) {
    console.error("Fetch profile failed:", error);
    Swal.fire("錯誤", "無法獲取會員資料", "error");
  }
};

onMounted(() => {
  const initialize = async () => {
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get("token");
    const source = urlParams.get("source");
    if (token) {
      localStorage.setItem("token", token);
      axios.defaults.headers.Authorization = `Bearer ${token}`;
      window.history.replaceState({}, document.title, "/");
    }
    if (source === "line") {
      await fetchProfile();
      location.href = location.href;
    } else {
      await fetchProfile();
    }
  };
  initialize();
});

const navigateToAdmin = () => {
  router.push("/admin/members");
};

const editProfile = () => {
  isEditing.value = true;
  editMember.value = {
    ...userStore.member,
    socialLinks: { ...(userStore.member.socialLinks || {}) },
  };
};

const cancelEdit = () => {
  isEditing.value = false;
  editMember.value = {
    ...userStore.member,
    socialLinks: { ...(userStore.member.socialLinks || {}) },
  };
};

const saveProfile = async () => {
  try {
    if (editMember.value.birth) {
      editMember.value.birth = new Date(editMember.value.birth)
        .toISOString()
        .split("T")[0];
    }
    const response = await axios.put("/ajax/secure/profile", editMember.value, {
      headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
    });
    if (response.data.success) {
      let updatedUser = { ...response.data.user };
      if (response.data.socialLinks) {
        updatedUser.socialLinks = response.data.socialLinks;
      }
      userStore.member = updatedUser;
      isEditing.value = false;
      Swal.fire("成功", "資料已更新！", "success");
      fetchProfile();
      console.log("Sending Updated Member Data:", editMember.value);
    } else {
      Swal.fire("錯誤", response.data.message, "error");
    }
  } catch (error) {
    console.error("Save profile failed:", error);
    Swal.fire("錯誤", "伺服器錯誤，請稍後再試", "error");
  }
  console.log("Submitting data:", editMember.value);
};

const uploadProfilePicture = async (event) => {
  const file = event.target.files[0];
  if (!file) return;
  const formData = new FormData();
  formData.append("file", file);
  formData.append("email", userStore.member.email);
  try {
    const response = await axios.post("/ajax/secure/upload-profile-picture", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    if (response.data.success) {
      userStore.member.profile_picture = response.data.profilePicture;
      Swal.fire("成功", "大頭貼已更新！", "success");
    } else {
      Swal.fire("錯誤", response.data.message, "error");
    }
  } catch (error) {
    console.error("圖片上傳失敗:", error);
    Swal.fire("錯誤", "伺服器錯誤，請稍後再試", "error");
  }
};

const logout = () => {
  localStorage.removeItem("token");
  userStore.logout();
  router.push("/secure/Login").then(() => {
    window.location.reload();
  });
};

const onImageError = (event) => {
  event.target.src = "預設圖片連結";
};
</script>

<style scoped>
.profile-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 30px;
  font-family: "Arial", sans-serif;
  background: linear-gradient(145deg, #ffffff, #f3f3f3);
  border-radius: 20px;
  box-shadow: 0 8px 15px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.profile-header {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
  border-bottom: 2px solid #e0e0e0;
  padding-bottom: 20px;
}

/* 保持圖片大小，不受右側區塊影響 */
.profile-picture {
  width: 130px;
  height: 130px;
  flex-shrink: 0; /* 防止圖片因 flex 收縮 */
  border-radius: 50%;
  object-fit: cover;
  margin-right: 25px;
  border: 4px solid #6c757d;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.profile-picture:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.2);
}

/* 會員資訊區：分為左右兩欄 */
.user-info {
  display: flex;
  flex-direction: column;
  width: 100%;
}

/* info-row：會員名稱與按鈕各自獨立 */
.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  margin-bottom: 10px;
}

/* 左側文字區 */
.text-info {
  /* 讓文字區不被影響 */
}
.text-info h1 {
  margin: 0;
  font-size: 28px;
  font-weight: bold;
  color: #343a40;
}
.welcome-text {
  font-size: 16px;
  color: #555;
  font-style: italic;
}

/* 右側操作按鈕區 */
.header-actions {
  display: flex;
  gap: 10px;
}

.btn-edit,
.btn-admin {
  padding: 14px 26px;
  font-size: 18px;
  border-radius: 12px;
  font-weight: bold;
  border: none;
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease, background 0.3s ease;
}

.btn-edit {
  background: linear-gradient(45deg, #007bff, #00c6ff);
  color: #fff;
}
.btn-edit:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.2);
  background: linear-gradient(45deg, #0056b3, #0086d1);
}

.btn-admin {
  background: linear-gradient(45deg, #6a11cb, #2575fc);
  color: #fff;
}
.btn-admin:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.2);
  background: linear-gradient(45deg, #5a0fb5, #1f63e0);
}

/* Profile details container */
.profile-details-container {
  margin-top: 20px;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 15px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.profile-details {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.detail-item {
  flex: 1 1 calc(50% - 20px);
  display: flex;
  flex-direction: column;
  background: #ffffff;
  border: 1px solid #e9ecef;
  border-radius: 10px;
  padding: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.detail-item:hover {
  transform: scale(1.02);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.detail-item dt {
  font-weight: bold;
  color: #495057;
  margin-bottom: 8px;
  font-size: 14px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.detail-item dd {
  margin: 0;
  color: #6c757d;
  font-size: 16px;
  font-weight: 500;
  word-break: break-word;
}

.edit-form-container {
  margin-top: 20px;
  padding: 25px;
  background: #f8f9fa;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.edit-form-container h4 {
  margin-bottom: 20px;
  font-size: 22px;
  color: #495057;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: bold;
  color: #495057;
}

.form-control {
  width: 100%;
  padding: 10px;
  border: 1px solid #ced4da;
  border-radius: 8px;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

.form-control:focus {
  border-color: #80bdff;
  box-shadow: 0 0 5px rgba(0, 123, 255, 0.5);
}

.btn {
  display: inline-block;
  padding: 12px 25px;
  border-radius: 8px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.3s ease, box-shadow 0.3s ease;
}

/* 其他按鈕樣式保持不變 */

.actions-row {
  display: flex;
  justify-content: flex-end;
  gap: 20px;
  margin-top: 30px;
}

/* 社群連結區塊 */
.social-links-section {
  margin-top: 20px;
  text-align: center;
}

.social-links-section h3 {
  margin-bottom: 15px;
  font-size: 20px;
  color: #333;
}

.social-icons {
  list-style: none;
  padding: 0;
  margin: 0 auto;
  display: flex;
  justify-content: center;
  gap: 15px;
}

.social-icons li a {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  color: #fff;
  text-decoration: none;
  transition: transform 0.3s ease, opacity 0.3s ease;
}

.social-icons li a.facebook {
  background-color: #3b5998;
}

.social-icons li a.instagram {
  background: radial-gradient(
    circle at 30% 107%,
    #fdf497 0%,
    #fdf497 5%,
    #fd5949 45%,
    #d6249f 60%,
    #285aeb 90%
  );
}

.social-icons li a:hover {
  transform: scale(1.1);
  opacity: 0.9;
}

/* 發文/規劃按鈕 (保持原有) */
#planningbutton {
  position: fixed;
  bottom: 50px;
  right: 50px;
  width: 100px;
  height: 100px;
  background-color: #84baf5;
  color: #fff;
  border-radius: 50%;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  text-align: center;
  font-size: 35px;
  font-weight: bold;
  text-decoration: none;
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
  white-space: normal;
  overflow-wrap: break-word;
  padding: 10px;
  transition: transform 0.2s, background-color 0.2s;
}

#planningbutton:hover {
  transform: scale(1.1);
  background-color: #5a95d5;
}

#blogbutton {
  position: fixed;
  bottom: 200px;
  right: 50px;
  width: 100px;
  height: 100px;
  background-color: #85a98f;
  color: #fff;
  border-radius: 50%;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  text-align: center;
  font-size: 35px;
  font-weight: bold;
  text-decoration: none;
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
  white-space: normal;
  overflow-wrap: break-word;
  padding: 10px;
  transition: transform 0.2s, background-color 0.2s;
}

#blogbutton:hover {
  transform: scale(1.1);
  background-color: #5a6c57;
}
</style>
