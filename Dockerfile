# 使用CentOS作为基础镜像
FROM centos:7

# # 更换为阿里云的yum源
RUN mv /etc/yum.repos.d/CentOS-Base.repo /etc/yum.repos.d/CentOS-Base.repo.backup && \
    curl -o /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo && \
    yum clean all && \
    yum makecache
# RUN sed -i 's|^mirrorlist=|#mirrorlist=|g' /etc/yum.repos.d/CentOS-Base.repo && \
#     sed -i 's|^#baseurl=http://mirror.centos.org|baseurl=https://mirrors.aliyun.com|g' /etc/yum.repos.d/CentOS-Base.repo

# 安装必要的工具和软件，包括curl
# RUN yum update -y && \
#     yum install -y java-1.8.0-openjdk-devel mysql-server nginx curl && \
#     yum clean all
RUN yum update -y && \
    yum install -y mysql-server nginx curl tar initscripts && \
    yum clean all

RUN curl -fSL -o /tmp/openjdk-17.tar.gz "https://download.oracle.com/java/17/archive/jdk-17.0.12_linux-x64_bin.tar.gz" && \
# 验证下载文件完整性
    if [ ! -s "/tmp/openjdk-17.tar.gz" ]; then echo "JDK 17 下载失败"; exit 1; fi && \
    # 解压到统一路径（移除版本号前缀，确保路径稳定）
    mkdir -p /usr/lib/jvm/jdk-17 && \
    tar -zxf /tmp/openjdk-17.tar.gz -C /usr/lib/jvm/jdk-17 --strip-components=1 && \
    # 验证解压结果（确保 java 命令存在）
    if [ ! -f "/usr/lib/jvm/jdk-17/bin/java" ]; then echo "JDK 17 解压失败"; exit 1; fi && \
    # 清理安装包
    rm -f /tmp/openjdk-17.tar.gz    

# 4. 配置 JDK 17 环境变量（关键：让系统和 Maven 识别 JDK）
ENV JAVA_HOME=/usr/lib/jvm/jdk-17
ENV PATH=$JAVA_HOME/bin:$PATH

# 验证 JDK 17 安装（这一步必须成功，否则构建中断）
RUN echo "当前 JDK 版本：" && java -version && \
    echo "当前 JAVA_HOME：$JAVA_HOME"

# 安装 Maven 3.9.11
RUN curl -fSL -o /tmp/apache-maven-3.9.11-bin.tar.gz "https://mirrors.aliyun.com/apache/maven/maven-3/3.9.11/binaries/apache-maven-3.9.11-bin.tar.gz" && \
    tar -zxf /tmp/apache-maven-3.9.11-bin.tar.gz -C /opt/ && \
    ln -s /opt/apache-maven-3.9.11/bin/mvn /usr/bin/mvn && \
    rm -f /tmp/apache-maven-3.9.11-bin.tar.gz

# 验证 Maven 安装及 JDK 关联（确保 Maven 使用 JDK 17）
RUN echo "当前 Maven 版本：" && mvn -v

# 设置工作目录
WORKDIR /app

# 将项目文件复制到容器中
COPY . .

# 复制Nginx配置文件
COPY nginx.conf /etc/nginx/nginx.conf

# 添加 Maven 配置文件
COPY settings.xml /root/.m2/settings.xml

# 编译项目
RUN mvn clean package

# 暴露服务端口
EXPOSE 8080

# 启动MySQL和Nginx
CMD \
    # 初始化 MySQL（首次启动必需）
    if [ ! -d /var/lib/mysql/mysql ]; then mysqld --initialize-insecure; fi && \
    # 直接启动 MySQL（后台运行，& 表示后台）
    /usr/sbin/mysqld & \
    # 直接启动 Nginx（后台运行）
    /usr/sbin/nginx & \
    # 启动 Java 服务（前台运行，作为容器主进程）
    java -jar target/back_server-0.0.1-SNAPSHOT.jar