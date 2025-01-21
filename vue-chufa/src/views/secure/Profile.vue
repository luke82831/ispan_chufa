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
        <h1>{{ member.name || "會員名稱" }}</h1>
        <p class="welcome-text">歡迎回來，{{ member.nickname || "暱稱" }}！</p>
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
      <button class="btn btn-primary" @click="editProfile">編輯資料</button>
    </div>

    <!-- Edit Form -->
    <div v-else id="edit-form" class="edit-form-container">
      <h4>編輯個人資料</h4>
      <form @submit.prevent="saveProfile">
        <div class="form-group">
          <label for="profilePicture">上傳大頭貼：</label>
          <input type="file" id="profilePicture" @change="uploadProfilePicture" class="form-control" />
        </div>
        <div class="form-group">
          <label for="name">姓名：</label>
          <input type="text" id="name" v-model="editMember.name" class="form-control" />
        </div>
        <div class="form-group">
          <label for="nickname">暱稱：</label>
          <input type="text" id="nickname" v-model="editMember.nickname" class="form-control" />
        </div>
        <div class="form-group">
          <label for="phoneNumber">手機號碼：</label>
          <input type="text" id="phoneNumber" v-model="editMember.phone_number" class="form-control" />
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
          <input type="email" id="email" v-model="editMember.email" class="form-control" />
        </div>
        <div class="form-group">
          <label for="bio">個人簡介：</label>
          <textarea id="bio" v-model="editMember.bio" class="form-control"></textarea>
        </div>
        <button type="submit" class="btn btn-save">儲存變更</button>
        <button type="button" class="btn btn-secondary" @click="cancelEdit">取消</button>
      </form>
    </div>

    <!-- 將管理員功能按鈕與登出按鈕放在同一排 -->
    <div class="actions-row">
      <button class="btn btn-secondary" v-if="isAdmin" @click="navigateToAdmin">
        管理會員資料
      </button>
      <button class="btn btn-logout" @click="logout">登出</button>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from '@/plugins/axios.js';
import Swal from 'sweetalert2';
import { useRouter } from 'vue-router';

const router = useRouter();

const isEditing = ref(false);
const profileLoaded = ref(false);

const member = ref({});
const editMember = ref({});

// 定義 isAdmin 變數並初始化
const isAdmin = ref(false);

const formatDate = (date) => {
  if (!date) return '';
  const options = { year: 'numeric', month: '2-digit', day: '2-digit' };
  return new Date(date).toLocaleDateString('zh-TW', options);
};

const fetchProfile = async () => {
  try {
    const token = localStorage.getItem('token');
    if (!token) {
      Swal.fire('錯誤', '未登入或登入已過期，請重新登入', 'error');
      router.push('/secure/Login');
      return;
      }
    const response = await axios.get('/ajax/secure/profile', {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
    });

    if (response.data.success) {
      member.value = response.data.user || {};
      editMember.value = { ...member.value };

      // 判斷是否為管理員
      isAdmin.value = response.data.user.role === 'ADMIN';
      console.log("User Role:", response.data.user.role);
      console.log("Is Admin:", isAdmin.value);
    } else {
      Swal.fire('錯誤', response.data.message, 'error');
    }
    profileLoaded.value = true;
  } catch (error) {
    console.error('Fetch profile failed:', error);
    Swal.fire('錯誤', '無法獲取會員資料', 'error');
  }
};
// 處理頁面載入和回調參數的邏輯
onMounted(() => {
    const initialize = async () => {
        // 定義 urlParams 並從當前窗口的 URL 中解析查詢參數
        const urlParams = new URLSearchParams(window.location.search);
        const token = urlParams.get('token');

        if (token) {
            // 保存 token 並清理 URL
            localStorage.setItem('token', token);
            axios.defaults.headers.Authorization = `Bearer ${token}`;
            window.history.replaceState({}, document.title, '/secure/Profile'); // 清除查詢參數
        } else {
            console.warn("Token not found in URL query parameters.");
        }

        // 嘗試抓取用戶資料
        await fetchProfile();
    };

    initialize(); // 呼叫初始化函數
});


// 管理員跳轉到會員管理頁面 (路徑不變)
const navigateToAdmin = () => {
  router.push('/admin/Role');
};

const editProfile = () => {
  isEditing.value = true;
  editMember.value = { ...member.value }; // 深拷貝資料
};

const cancelEdit = () => {
  isEditing.value = false;
  editMember.value = { ...member.value }; // 恢復到未編輯狀態
};

const saveProfile = async () => {
  try {
    // 格式化生日欄位
    if (editMember.value.birth) {
      editMember.value.birth = new Date(editMember.value.birth).toISOString().split('T')[0];
    }

    const response = await axios.put('/ajax/secure/profile', editMember.value, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
    });

    if (response.data.success) {
      member.value = { ...editMember.value }; // 更新畫面資料
      isEditing.value = false;
      Swal.fire('成功', '資料已更新！', 'success');
      fetchProfile(); // 重新抓取最新資料

      console.log("Sending Updated Member Data:", editMember.value);
    } else {
      Swal.fire('錯誤', response.data.message, 'error');
    }
  } catch (error) {
    console.error('Save profile failed:', error);
    Swal.fire('錯誤', '伺服器錯誤，請稍後再試', 'error');
  }

  console.log('Submitting data:', editMember.value);
};

const uploadProfilePicture = async (event) => {
  const file = event.target.files[0];
  if (!file) return;

  const formData = new FormData();
  formData.append('file', file);
  formData.append('email', member.value.email);

  try {
    const response = await axios.post('/ajax/secure/upload-profile-picture', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });

    if (response.data.success) {
      member.value.profile_picture = response.data.profilePicture; // 更新頭像
      Swal.fire('成功', '大頭貼已更新！', 'success');
    } else {
      Swal.fire('錯誤', response.data.message, 'error');
    }
  } catch (error) {
    console.error('圖片上傳失敗:', error);
    Swal.fire('錯誤', '伺服器錯誤，請稍後再試', 'error');
  }
};

const logout = () => {
  localStorage.removeItem('token');
  router.push('/secure/Login');
};

fetchProfile();
</script>

<style scoped>
.profile-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 30px;
  font-family: 'Arial', sans-serif;
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

.profile-picture {
  width: 130px;
  height: 130px;
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

.user-info h1 {
  margin: 0;
  font-size: 28px;
  font-weight: bold;
  color: #343a40;
}

.welcome-text {
  margin-top: 5px;
  font-size: 16px;
  color: #555;
  font-style: italic;
}

.profile-details-container {
  margin-top: 20px;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 15px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.profile-details {
  display: flex;
  flex-wrap: wrap; /* 允許換行 */
  gap: 20px;       /* 每個欄位的間距 */
}

.detail-item {
  flex: 1 1 calc(50% - 20px); /* 每個項目占50%寬度，左右間距20px */
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

.btn-primary {
  background: #007bff;
  color: white;
  border: none;
}

.btn-primary:hover {
  background: #0056b3;
  box-shadow: 0 4px 10px rgba(0, 91, 187, 0.4);
}

.btn-secondary {
  background: #6c757d;
  color: white;
  border: none;
}

.btn-secondary:hover {
  background: #5a6268;
  box-shadow: 0 4px 10px rgba(90, 98, 104, 0.4);
}

.btn-logout {
  background: #dc3545;
  color: white;
  border: none;
}

.btn-logout:hover {
  background: #c82333;
  box-shadow: 0 4px 10px rgba(200, 35, 51, 0.4);
}

.btn-save {
  background: #28a745;
  color: white;
  border: none;
}

.btn-save:hover {
  background: #218838;
  box-shadow: 0 4px 10px rgba(33, 136, 56, 0.4);
}

/* 統一放在同一排的操作按鈕列 */
.actions-row {
  display: flex;
  justify-content: flex-end; /* 也可以改成 space-between 或其他排版方式 */
  gap: 20px;                 /* 按鈕間距 */
  margin-top: 30px;          /* 與上方區塊拉開一點距離 */
}
</style>
