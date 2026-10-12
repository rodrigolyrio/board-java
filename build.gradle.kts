plugins {
    id("java")
    id("application")
}

group = "br.com.dio"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Controle de versão do banco de dados (migrations)
    implementation("org.liquibase:liquibase-core:4.29.1")
    // Banco embutido (padrão): não exige instalar nada
    runtimeOnly("com.h2database:h2:2.3.232")
    // Driver opcional, caso queira usar MySQL (veja o README)
    runtimeOnly("com.mysql:mysql-connector-j:8.4.0")
}

// Compila com o JDK instalado, gerando bytecode compatível com Java 17+
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(17)
}

application {
    mainClass.set("br.com.dio.board.Main")
    // A saída usa a codificação do próprio terminal (evita acentos quebrados no Windows).
    applicationDefaultJvmArgs = listOf("-Dfile.encoding=UTF-8")
}

tasks.named<JavaExec>("run") {
    // Necessário para o jogo/menu ler do teclado quando executado via Gradle
    standardInput = System.`in`
}
