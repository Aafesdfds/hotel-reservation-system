# ---- 构建阶段：用 Maven 把项目打成 war ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B clean package -DskipTests

# ---- 运行阶段：基于官方 Tomcat 9 镜像部署 ----
FROM tomcat:9.0-jdk17-temurin
# 清掉自带的示例应用，把我们的 war 作为根应用（ROOT.war -> 访问路径 /）
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/ROOT.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]
