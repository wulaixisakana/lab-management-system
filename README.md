# 实验室管理系统

基于 **Vue 3 + Spring Boot** 的实验室管理系统，包含设备管理预约和人员考勤功能。

## 功能模块

- **用户管理**：用户注册、登录、权限控制（管理员/教师/学生）
- **设备管理**：设备的增删改查、分类管理、状态管理
- **设备预约**：设备预约申请、审核通过/拒绝、预约取消
- **考勤管理**：签到签退、考勤记录查询、在岗时长统计

## 技术栈

### 后端
- Spring Boot 2.7.18
- MyBatis
- MySQL 8.0
- JWT 认证

### 前端
- Vue 3
- Vite
- Element Plus
- Pinia
- Vue Router
- Axios

## 项目结构

```
lab-management-system/
├── backend/                 # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/lab/
│   │   │   │   ├── config/          # 配置类
│   │   │   │   ├── controller/      # 控制器
│   │   │   │   ├── entity/          # 实体类
│   │   │   │   ├── mapper/          # MyBatis Mapper
│   │   │   │   ├── service/         # 业务逻辑
│   │   │   │   ├── common/          # 公共类
│   │   │   │   ├── interceptor/     # 拦截器
│   │   │   │   └── util/            # 工具类
│   │   │   └── resources/
│   │   │       ├── mapper/          # MyBatis XML
│   │   │       └── sql/             # SQL脚本
│   │   └── test/
│   └── pom.xml
│
└── frontend/                # 前端项目
    ├── src/
    │   ├── api/             # API接口
    │   ├── layouts/         # 布局组件
    │   ├── router/          # 路由
    │   ├── stores/          # 状态管理
    │   ├── utils/           # 工具函数
    │   ├── views/           # 页面组件
    │   ├── App.vue
    │   └── main.js
    ├── index.html
    ├── package.json
    └── vite.config.js
```

## 快速开始

### 环境要求

- JDK 1.8+
- Node.js 16+
- MySQL 8.0+
- Maven 3.6+

### 1. 数据库初始化

创建数据库并执行初始化脚本：

```bash
# 登录MySQL
mysql -u root -p

# 执行初始化脚本
source backend/src/main/resources/sql/init.sql
```

### 2. 后端启动

```bash
cd backend

# 修改配置文件中的数据库连接信息
# vim src/main/resources/application.yml

# 启动后端服务
mvn spring-boot:run

# 或使用IDE运行 LabManagementApplication.java
```

后端服务将在 `http://localhost:8080` 启动

### 3. 前端启动

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端服务将在 `http://localhost:3000` 启动

## 默认账号

| 用户类型 | 用户名 | 密码 |
|---------|--------|------|
| 管理员   | admin  | admin123 |
| 教师     | teacher | teacher123 |
| 学生     | student | student123 |

## API接口

### 用户相关
- `POST /user/login` - 用户登录
- `POST /user/register` - 用户注册
- `GET /user/info` - 获取当前用户信息
- `GET /user/list` - 获取用户列表（管理员）
- `POST /user/update` - 更新用户信息
- `DELETE /user/{id}` - 删除用户（管理员）

### 设备相关
- `GET /equipment/list` - 获取设备列表
- `GET /equipment/{id}` - 获取设备详情
- `POST /equipment/save` - 新增设备
- `POST /equipment/update` - 更新设备
- `DELETE /equipment/{id}` - 删除设备

### 预约相关
- `GET /reservation/list` - 获取预约列表
- `GET /reservation/my` - 获取我的预约
- `POST /reservation/create` - 创建预约
- `POST /reservation/{id}/approve` - 审核通过
- `POST /reservation/{id}/reject` - 审核拒绝
- `POST /reservation/{id}/cancel` - 取消预约
- `DELETE /reservation/{id}` - 删除预约

### 考勤相关
- `GET /attendance/list` - 获取考勤列表
- `GET /attendance/my` - 获取我的考勤
- `GET /attendance/today` - 获取今日考勤状态
- `POST /attendance/checkin` - 签到
- `POST /attendance/checkout` - 签退

## 权限说明

- **管理员（admin）**：所有功能权限
- **教师（teacher）**：设备管理（除删除）、预约审核、考勤查询
- **学生（student）**：查看设备、创建预约、签到签退、查看个人记录

## 开发说明

### 后端配置

后端配置文件位于 `backend/src/main/resources/application.yml`，可根据需要修改：
- 服务端口
- 数据库连接信息
- JWT配置

### 前端配置

前端配置文件位于 `frontend/vite.config.js`，可根据需要修改：
- 开发服务器端口
- API代理配置

## 注意事项

1. 首次运行前请确保MySQL服务已启动
2. 数据库脚本会自动创建测试数据
3. 默认密码为明文存储的MD5值，生产环境请使用BCrypt等加密方式
4. JWT密钥建议在生产环境中更换为更复杂的值
