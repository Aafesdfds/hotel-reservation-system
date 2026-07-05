# 宾馆房间预订与管理系统

面向中小型宾馆的客房预订与后台管理系统。用户可以在前台浏览房型、按日期查询空房、在线预订；
管理员在后台管理房型、房间、订单和会员，处理房态流转，并调用大模型生成经营分析。

## 技术栈

- 后端：Java Servlet + JDBC（原生）
- 前端：JSP + JSTL + JavaScript，图表用 ECharts
- 数据库：MySQL 8
- 服务器：Tomcat 9
- AI：HTTP 调用 DeepSeek 大模型（OpenAI 兼容接口）
- 部署：Docker + docker-compose
- 构建：Maven（打成 war 包）

## 主要功能

用户端
- 注册、登录
- 房型浏览、房型详情
- 按入住/退房日期查询各房型实时空房数量
- 在线预订（系统自动分配空房）、查看和取消订单
- 个人中心（改资料、改密码）

管理端
- 仪表盘：当日入住/退房、在住数、实时入住率、房态看板、月度营收与入住率图表
- 房型管理：增删改查
- 房间管理：增删改、房态流转（空闲 / 已入住 / 待清洁 / 清洁中 / 已清洁 / 维修）
- 订单管理：查询、办理入住、办理退房、取消
- 会员管理：查看注册用户
- 数据统计：月度营收、入住率、房型分布、订单状态分布
- AI 经营分析：调用大模型根据经营数据生成分析和房价调整建议

## 并发处理

多人同时预订同一房型时，系统用数据库事务加行锁（`SELECT ... FOR UPDATE`）把同房型的下单串行化：
先锁住该房型全部房间行，再在锁的保护下找一间该日期空闲的房分配。有几间空房就只能成交几单，
不会把同一间房卖给两个人。核心逻辑见 `service/BookingService.java` 和 `dao/RoomDao.java`。

## 运行方式

### 方式一：Docker（推荐）

```
docker compose up --build
```

会启动 MySQL 和 Tomcat 两个容器，数据库首次启动自动导入 `db/init.sql`。
启动后访问 http://localhost:8080

数据库连接和大模型密钥都通过环境变量注入，配置见 `.env.example`（复制成 `.env` 填写）。

### 方式二：本地 Tomcat + MySQL

1. 建库导数据：`mysql -u root -p < db/init.sql`
2. 按需修改 `src/main/resources/db.properties` 或设置环境变量 `DB_HOST/DB_PORT/DB_NAME/DB_USER/DB_PASSWORD`
3. 配置大模型密钥（环境变量 `AI_API_KEY`）
4. `mvn package` 打出 `target/ROOT.war`，丢进 Tomcat 的 webapps 目录

## 默认账号

- 管理员：admin / admin123
- 普通用户：user / 123456（其余演示用户密码均为 123456）

## 分支说明

- `main`：稳定版本
- `develop`：集成分支
- `feature/front-booking`：前台预订功能
- `feature/admin-manage`：后台管理功能
- `feature/ai-analysis`：AI 分析与数据统计
