
-- 部门表
CREATE TABLE department (
    department_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,   -- 部门ID（主键，自增）
    department_name VARCHAR(255) UNIQUE                            -- 部门名称
) 
CHARACTER SET utf8;  -- 设置字符集为utf8

-- 岗位表：增加岗位时需要选择增加那个岗位
CREATE TABLE position (
    position_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,    -- 岗位ID（主键，自增）
		department_id INT NOT NULL，                        -- 部门ID（外键）
    position_name VARCHAR(255)                              -- 岗位名称 
) 
CHARACTER SET utf8;  -- 设置字符集为utf8

-- 员工表
CREATE TABLE employee (
    employee_id INT AUTO_INCREMENT PRIMARY KEY,              -- 员工ID（主键，自增）
    name VARCHAR(255) NOT NULL,                              -- 姓名（非空）
    password VARCHAR(255),                                   -- 密码
    age INT,                                                 -- 年龄
    position_id INT,                                         -- 岗位ID（外键）
    department_id INT,                                       -- 部门ID（外键）
    phone VARCHAR(20),                                       -- 手机号
    email VARCHAR(255),                                      -- 邮箱
		permissions INT NOT NUll                             -- 员工权限
)
CHARACTER SET utf8;  -- 设置字符集为utf8

-- 考勤表
CREATE TABLE attendance (
    attendance_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,  -- 考勤记录ID（主键，自增）
    employee_id INT NOT NULL,                               -- 员工ID（外键）
    department_id INT NOT NULL,                   -- 部门ID（外键）
    work_date DATETIME,                                      -- 打卡时间
		status INT                                              -- 打卡状态
) CHARACTER SET utf8;


-- 请假表
CREATE TABLE leave_request (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,   -- 请假请求ID（主键，自增）
    employee_id INT NOT NULL,                     -- 员工ID（外键）
		department_id INT NOT NULL,                   -- 部门ID（外键）
		leave_time DATETIME,                          -- 请假时间
    leave_days INT,                               -- 请假天数
    type_id VARCHAR(50),                          -- 请假类型
    leave_reason VARCHAR(255),                    -- 请假原因
    approval_status INT                   -- 审核状态（例如“待审核”，“已批准”，“已拒绝”）
)
CHARACTER SET utf8;  -- 设置字符集为utf8

CREATE TABLE leave_type (
    type_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,  -- 请假类型id
    type VARCHAR(50)                                  -- 请假类型
)
CHARACTER SET utf8;  -- 设置字符集为utf8

