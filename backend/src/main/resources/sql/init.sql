-- 创建数据库
CREATE DATABASE IF NOT EXISTS lab_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE lab_management;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
    `phone` VARCHAR(20) COMMENT '电话',
    `email` VARCHAR(100) COMMENT '邮箱',
    `role` VARCHAR(20) NOT NULL DEFAULT 'student' COMMENT '角色：admin/teacher/student',
    `status` VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态：active/disabled',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 设备表
CREATE TABLE IF NOT EXISTS `equipment` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL COMMENT '设备名称',
    `code` VARCHAR(50) NOT NULL UNIQUE COMMENT '设备编号',
    `category` VARCHAR(50) NOT NULL COMMENT '设备分类',
    `brand` VARCHAR(50) COMMENT '品牌',
    `model` VARCHAR(100) COMMENT '型号',
    `location` VARCHAR(100) COMMENT '存放位置',
    `status` VARCHAR(20) NOT NULL DEFAULT 'available' COMMENT '状态：available/in_use/maintenance/unavailable',
    `price` DECIMAL(10,2) COMMENT '价格',
    `description` TEXT COMMENT '描述',
    `purchase_date` DATE COMMENT '购买日期',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备表';

-- 预约表
CREATE TABLE IF NOT EXISTS `reservation` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `equipment_id` INT NOT NULL COMMENT '设备ID',
    `user_id` INT NOT NULL COMMENT '用户ID',
    `user_name` VARCHAR(50) NOT NULL COMMENT '用户姓名',
    `equipment_name` VARCHAR(100) NOT NULL COMMENT '设备名称',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME NOT NULL COMMENT '结束时间',
    `purpose` VARCHAR(200) COMMENT '使用目的',
    `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态：pending/approved/rejected/cancelled',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`equipment_id`) REFERENCES equipment(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备预约表';

-- 考勤表
CREATE TABLE IF NOT EXISTS `attendance` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL COMMENT '用户ID',
    `user_name` VARCHAR(50) NOT NULL COMMENT '用户姓名',
    `check_in_time` DATETIME NOT NULL COMMENT '签到时间',
    `check_out_time` DATETIME COMMENT '签退时间',
    `status` VARCHAR(20) NOT NULL DEFAULT 'present' COMMENT '状态：present/late/early_leave/absent',
    `duration` INT COMMENT '在岗时长（分钟）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤表';

-- 插入初始管理员账号（密码：admin123）
INSERT INTO `user` (`username`, `password`, `real_name`, `phone`, `email`, `role`, `status`)
VALUES ('admin', '0192023a7bbd73250516f069df18b500', '系统管理员', '13800138000', 'admin@lab.com', 'admin', 'active')
ON DUPLICATE KEY UPDATE `username` = `username`;

-- 插入测试教师账号（密码：teacher123）
INSERT INTO `user` (`username`, `password`, `real_name`, `phone`, `email`, `role`, `status`)
VALUES ('teacher', '6adfb183a4a2c94a2f92dab5ade762a4', '测试教师', '13800138001', 'teacher@lab.com', 'teacher', 'active')
ON DUPLICATE KEY UPDATE `username` = `username`;

-- 插入测试学生账号（密码：student123）
INSERT INTO `user` (`username`, `password`, `real_name`, `phone`, `email`, `role`, `status`)
VALUES ('student', '7549d875442fd40ef4d81e4a6b9333c8', '测试学生', '13800138002', 'student@lab.com', 'student', 'active')
ON DUPLICATE KEY UPDATE `username` = `username`;

-- 实验室表
CREATE TABLE IF NOT EXISTS `laboratory` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL COMMENT '实验室名称',
    `code` VARCHAR(50) NOT NULL UNIQUE COMMENT '实验室编号',
    `building` VARCHAR(50) COMMENT '楼栋',
    `floor` VARCHAR(20) COMMENT '楼层',
    `room_number` VARCHAR(20) COMMENT '房间号',
    `area` DECIMAL(10,2) COMMENT '面积（平方米）',
    `capacity` INT COMMENT '容纳人数',
    `supervisor` VARCHAR(50) COMMENT '负责人',
    `phone` VARCHAR(20) COMMENT '联系电话',
    `status` VARCHAR(20) NOT NULL DEFAULT 'available' COMMENT '状态：available/maintenance/unavailable',
    `description` TEXT COMMENT '描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实验室表';

-- 插入测试设备数据
INSERT INTO `equipment` (`name`, `code`, `category`, `brand`, `model`, `location`, `status`, `price`, `description`)
VALUES
    ('电子显微镜', 'EQ001', '精密仪器', '奥林巴斯', 'BX53', '实验室101', 'available', 150000.00, '高倍率电子显微镜'),
    ('离心机', 'EQ002', '分离设备', 'Eppendorf', '5424', '实验室102', 'available', 25000.00, '高速离心机'),
    ('PCR仪', 'EQ003', '分析仪器', 'Bio-Rad', 'T100', '实验室103', 'available', 35000.00, '热循环仪'),
    ('分光光度计', 'EQ004', '分析仪器', '尤尼柯', 'UV-2000', '实验室104', 'available', 12000.00, '紫外可见分光光度计'),
    ('超低温冰箱', 'EQ005', '储存设备', '海尔', 'DW-86L388', '实验室105', 'available', 28000.00, '-86℃超低温保存箱'),
    ('恒温培养箱', 'EQ006', '培养设备', '博迅', 'SPX-150', '实验室106', 'maintenance', 8000.00, '生化培养箱'),
    ('天平', 'EQ007', '计量仪器', '梅特勒', 'ME204', '实验室107', 'available', 5000.00, '分析天平'),
    ('pH计', 'EQ008', '检测仪器', '哈希', 'HQ11d', '实验室108', 'available', 3500.00, '便携式pH计')
ON DUPLICATE KEY UPDATE `code` = `code`;

-- 插入测试实验室数据
INSERT INTO `laboratory` (`name`, `code`, `building`, `floor`, `room_number`, `area`, `capacity`, `supervisor`, `phone`, `status`, `description`)
VALUES
    ('化学分析实验室', 'LAB001', '实验楼A', '1F', '101', 45.5, 20, '张老师', '13800001001', 'available', '用于化学分析与检测'),
    ('生物培养实验室', 'LAB002', '实验楼A', '2F', '201', 60.0, 15, '李老师', '13800001002', 'available', '用于微生物培养'),
    ('物理测量实验室', 'LAB003', '实验楼B', '1F', '101', 50.0, 18, '王老师', '13800001003', 'maintenance', '用于精密物理测量'),
    ('电子技术实验室', 'LAB004', '实验楼B', '2F', '201', 55.0, 25, '赵老师', '13800001004', 'available', '用于电子电路实验'),
    ('材料科学实验室', 'LAB005', '实验楼C', '1F', '101', 80.0, 30, '刘老师', '13800001005', 'available', '用于材料性能测试')
ON DUPLICATE KEY UPDATE `code` = `code`;
