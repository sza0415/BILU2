-- 创建案件表
DROP TABLE IF EXISTS `case`;
CREATE TABLE `case` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `case_name` varchar(255) NOT NULL COMMENT '案件名称',
                        `case_number` varchar(50) NOT NULL COMMENT '案件编号',
                        `case_type` varchar(50) NOT NULL COMMENT '案件类型',
                        `start_date` date NOT NULL COMMENT '立案日期',
                        `end_date` date DEFAULT NULL COMMENT '结案日期',
                        `status` varchar(50) NOT NULL COMMENT '案件状态',
                        `description` text COMMENT '案件描述',
                        `created_at` datetime NOT NULL COMMENT '创建时间',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_case_number` (`case_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='案件表';

-- 创建笔录记录表
DROP TABLE IF EXISTS `tb_blog_record`;
CREATE TABLE `case_record` (
                               `id` bigint NOT NULL AUTO_INCREMENT,
                               `record_name` varchar(255) NOT NULL COMMENT '笔录名称',
                               `inquiry_time` datetime NOT NULL COMMENT '询问时间',
                               `inquiry_address` varchar(255) NOT NULL COMMENT '询问地点',
                               `inquirer_name` varchar(255) NOT NULL COMMENT '询问人',
                               `assistant_name` varchar(255) NOT NULL COMMENT '记录人',
                               `inquiry_id` varchar(255) NOT NULL COMMENT '询问人ID',
                               `text_content` text NOT NULL COMMENT '笔录内容',
                               `content_hash` varchar(32) DEFAULT NULL COMMENT '内容哈希值',
                               `video_file_path` varchar(500) DEFAULT NULL COMMENT '关联视频文件路径',
                               `created_at` datetime NOT NULL COMMENT '创建时间',
                               `case_id` bigint DEFAULT NULL COMMENT '所属案件ID',
                               PRIMARY KEY (`id`),
                               KEY `idx_case_id` (`case_id`),
                               CONSTRAINT `fk_case_record_case` FOREIGN KEY (`case_id`) REFERENCES `case` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='笔录记录表';

-- 创建保密问答模板表
DROP TABLE IF EXISTS `secrecy_template`;
CREATE TABLE `secrecy_template` (
                                    `id` bigint NOT NULL AUTO_INCREMENT,
                                    `template_type` varchar(50) NOT NULL COMMENT '模板类型',
                                    `question` text NOT NULL COMMENT '问题',
                                    `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序',
                                    PRIMARY KEY (`id`),
                                    KEY `idx_template_type` (`template_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='保密问答模板';

-- 插入案件数据
INSERT INTO `case` (case_name, case_number, case_type, start_date, end_date, status, description, created_at) VALUES 
('贪污受贿案', 'CASE-2023-001', '刑事案件', '2023-01-15', '2023-05-20', '已结案', '某国企高管涉嫌贪污受贿案', '2023-01-15 09:30:00'),
('合同纠纷案', 'CASE-2023-002', '民事案件', '2023-02-10', NULL, '审理中', '两家企业因合同履行问题产生纠纷', '2023-02-10 14:20:00'),
('行政许可案', 'CASE-2023-003', '行政案件', '2023-03-05', NULL, '调查中', '某公司不服行政机关的许可决定提起行政诉讼', '2023-03-05 10:15:00'),
('知识产权侵权案', 'CASE-2023-004', '民事案件', '2023-04-12', '2023-07-30', '已结案', '涉及商标侵权的知识产权纠纷案件', '2023-04-12 11:45:00'),
('环境污染案', 'CASE-2023-005', '刑事案件', '2023-05-18', NULL, '调查中', '某工厂涉嫌违法排放有毒物质污染环境', '2023-05-18 16:30:00');

-- 插入笔录记录数据
INSERT INTO `case_record` (record_name, inquiry_time, inquiry_address, inquirer_name, assistant_name, inquiry_id, text_content, content_hash, video_file_path, created_at, case_id) VALUES 
('首次询问笔录', '2023-01-16 10:00:00', '北京市公安局海淀分局审讯室', '李警官', '王书记', 'POLICE-001', '问：请问你是何时接触到该项目的？\n答：我是在2022年3月份通过张总接触到该项目的。\n问：项目中你主要负责什么工作？\n答：我主要负责项目资金的划拨和使用监管。', 'a1b2c3d4e5f6g7h8i9j0', '/videos/record_001_20230116.mp4', '2023-01-16 12:30:00', 1),
('关键证人询问笔录', '2023-02-15 14:30:00', '北京市第二中级人民法院询问室', '赵法官', '钱书记', 'JUDGE-002', '问：请描述您见到合同签署的全过程。\n答：2022年12月5日上午，我在公司会议室见证了双方代表在合同上签字，当时在场的有...\n问：签署过程中是否有人表示异议？\n答：没有，双方代表都确认了合同条款后才签字的。', 'b2c3d4e5f6g7h8i9j0k1', '/videos/record_002_20230215.mp4', '2023-02-15 16:45:00', 2),
('现场勘验笔录', '2023-03-10 09:15:00', '北京市朝阳区某办公楼', '孙调查员', '周记录员', 'INVEST-003', '现场勘验情况如下：办公室大门完好，无强制进入痕迹。室内文件柜打开，多份文件散落在地。电脑主机被带走，现场遗留有...\n问：您最后一次离开办公室是什么时间？\n答：昨天下午6点左右，我确认锁好了门窗。', 'c3d4e5f6g7h8i9j0k1l2', '/videos/record_003_20230310.mp4', '2023-03-10 11:20:00', 3),
('嫌疑人讯问笔录', '2023-04-20 15:00:00', '北京市公安局朝阳分局审讯室', '吴警官', '郑书记', 'POLICE-004', '问：请交代你侵犯他人知识产权的经过。\n答：我公司在2022年10月开始使用该logo，当时不知道已被他人注册为商标...\n问：在收到律师函后你们采取了什么措施？\n答：我们立即停止了使用，并联系对方希望协商解决。', 'd4e5f6g7h8i9j0k1l2m3', '/videos/record_004_20230420.mp4', '2023-04-20 17:30:00', 4),
('工厂负责人询问笔录', '2023-05-20 11:00:00', '环保局调查室', '冯调查员', '陈记录员', 'ENV-005', '问：贵公司的废水处理设施是何时建成的？\n答：2020年建成投入使用的。\n问：请说明5月10日晚间排污管道故障的具体情况。\n答：当晚设备显示数值正常，但现在回想可能监测系统被人为调整过...', 'e5f6g7h8i9j0k1l2m3n4', '/videos/record_005_20230520.mp4', '2023-05-20 13:15:00', 5),
('证人补充询问笔录', '2023-01-20 14:00:00', '北京市公安局海淀分局询问室', '李警官', '王书记', 'POLICE-001', '问：请详细描述您看到嫌疑人收受贿赂的过程。\n答：那天是2022年8月15日下午，我在办公室门口看到张总递给他一个棕色信封，他接过后立即放入抽屉并锁上了。\n问：当时还有其他人在场吗？\n答：没有其他人，只有他们两个人。', 'f6g7h8i9j0k1l2m3n4o5', '/videos/record_006_20230120.mp4', '2023-01-20 16:00:00', 1),
('合同细节询问笔录', '2023-02-25 10:30:00', '北京市第二中级人民法院询问室', '赵法官', '钱书记', 'JUDGE-002', '问：合同第三条关于付款方式的约定是如何确定的？\n答：双方经过三轮谈判后确定的，原本甲方要求一次性付款，最后改为分三期...\n问：变更条款是谁提出的？\n答：是乙方提出的，理由是资金周转问题。', 'g7h8i9j0k1l2m3n4o5p6', '/videos/record_007_20230225.mp4', '2023-02-25 12:45:00', 2);

-- 插入保密问答模板数据
INSERT INTO `secrecy_template` (template_type, question, sort_order) VALUES 
('secrecy_system', '您所在单位是否建立了保密管理制度？请详细说明。', 1),
('secrecy_system', '单位保密工作责任制如何落实？各级责任人是如何划分的？', 2),
('secrecy_system', '贵单位近三年内是否发生过失密事件？如有，请说明处理情况。', 3),
('secrecy_system', '涉密人员上岗前是否进行保密教育培训？培训内容和频次如何？', 4),
('secrecy_system', '单位内部涉密文件的流转程序是怎样的？', 5),

('military_research', '军工项目研究人员的保密审查程序是什么？', 1),
('military_research', '研究数据如何分级管理？不同密级的访问权限如何控制？', 2),
('military_research', '外来人员接触军工项目的审批流程是什么？', 3),
('military_research', '项目成果对外发布前的保密审查流程是怎样的？', 4),
('military_research', '涉密设备和样品的存放管理制度是什么？', 5),

('financial_secrecy', '金融机构客户信息保密措施有哪些？', 1),
('financial_secrecy', '重大金融决策的保密等级如何确定？', 2),
('financial_secrecy', '内部交易监控系统如何防范信息泄露？', 3),
('financial_secrecy', '涉及国家金融安全的信息如何特殊保护？', 4),
('financial_secrecy', '员工离职时的保密协议执行情况如何？', 5),

('medical_secrecy', '患者隐私信息的获取和使用规范是什么？', 1),
('medical_secrecy', '医疗数据在科研使用时如何匿名化处理？', 2),
('medical_secrecy', '特殊传染病患者信息的保密措施有哪些？', 3),
('medical_secrecy', '医院信息系统的访问权限如何分级管理？', 4),
('medical_secrecy', '医疗事故调查中的信息保密规定是什么？', 5),

('internal_secrecy_check', '内部保密检查的频率和覆盖范围是什么？', 1),
('internal_secrecy_check', '发现泄密隐患后的处置流程是怎样的？', 2),
('internal_secrecy_check', '保密自查报告的编制和提交程序是什么？', 3),
('internal_secrecy_check', '保密整改措施的落实和验收标准是什么？', 4),
('internal_secrecy_check', '保密检查结果如何反馈和应用？', 5),

('secrecy_case_interrogation', '请详述您获知涉密信息的时间、地点和途径。', 1),
('secrecy_case_interrogation', '您将涉密信息传递给了哪些人？请详细说明传递过程。', 2),
('secrecy_case_interrogation', '您是否意识到所接触的信息属于国家秘密？何时意识到的？', 3),
('secrecy_case_interrogation', '您在传播涉密信息时，采取了哪些防范措施？', 4),
('secrecy_case_interrogation', '您是否收到过任何与泄密相关的利益或好处？详细说明。', 5);

-- 创建管理员表
DROP TABLE IF EXISTS `admin_user`;
CREATE TABLE `admin_user` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `username` varchar(50) NOT NULL COMMENT '用户名',
    `password` varchar(128) NOT NULL COMMENT '密码（加密存储）',
    `phone` varchar(20) DEFAULT NULL COMMENT '手机号码',
    `role` varchar(20) NOT NULL DEFAULT 'user' COMMENT '角色（admin/user）',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态（1-正常，0-禁用）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员用户表';
-- 插入初始管理员数据（密码为md5加密的'admin123'）
INSERT INTO `admin_user` (username, password, role, status ) VALUES 
('admin', 'e10adc3949ba59abbe56e057f20f883e', 'admin', 1);