# Hibernate Conventions   [![Build Status](https://api.shippable.com/projects/540e74613479c5ea8f9e6238/badge?branchName=master)](https://app.shippable.com/projects/540e74613479c5ea8f9e6238/builds/latest)   [ ![Download](https://api.bintray.com/packages/andreluiznsilva/maven/hibernate-conventions/images/download.svg) ](https://bintray.com/andreluiznsilva/maven/hibernate-conventions/_latestVersion)

Plugin para Hibernate que permite validar e convencionar o mapeamento do hibernate de modo e gerar DDLs, SQLs e padrões de scripts de banco de forma mais simples e padronizada.

A validação permite, por exemplo, determinar os tamanhos máximos e mínimos dos nomes das colunas, tabelas e outros objetos de banco de dados gerados pelo hibernate.

A convenção permite, assim como o NamingStrategy (um extenção dele), definir convenções de nomes para indices, foreign keys, unique keys, sequences e outros objetos. Também é possível convencionar tipos de dados do banco para tipos Java, se a necessidade de mapear campo a campo.

Para instalar, basta adicionar o jar no projeto. Atualmente existe duas configurações que podem ser feitas no persistence.xml, são elas:

#### hibernate.conventions.maxLength (default = 255) 
Permite definir o tamanho máximo de carateres para os nomes de tabelas, sequences, colunas, etc. Por exemplo, o Oracle permite somente 30 carateres. Assim bastaria configurar da seguinte forma:

	<property name="hibernate.conventions.maxLength" value="30" />
	
#### hibernate.ejb.naming_strategy (default = hibernate.conventions.strategy.DefaultConventionNamingStrategy)
Permite configurar o NammingStrategy usado no Hibernate. Se esta classe implementar ConventionNamingStrategy, ela será usada para gerar as conveções de nomes para os objetos do banco de dados. Exemplo:

	<property name="hibernate.ejb.naming_strategy" value="hibernate.conventions.dummy.TestConventionNamingStrategy" />

## Setup

Tecnologies       | Version
------------------|--------------------
Java              | 6
Gradle            | 2.x
Hibernate         | 4.X
    
## Build
    
### Generate eclipse files

  gradle eclipse

### Compile

  gradle build

### Import

#### Gradle

    repositories {
        maven {
            url "https://raw.githubusercontent.com/andreluiznsilva/hibernate-conventions/mvn-repo"
        }
    }

    dependencies {
        compile 'hibernate-conventions:hibernate-conventions:0.0.1-SNAPSHOT'       
    }
  
#### Maven

	<repositories>
		<repository>
			<id>hibernate-conventions</id>
			<url>https://raw.githubusercontent.com/andreluiznsilva/hibernate-conventions/mvn-repo</url>
			<releases>
				<enabled>true</enabled>
			</releases>
			<snapshots>
				<enabled>true</enabled>
			</snapshots>
		</repository>
	</repositories>

	<dependencies>
		<dependency>
			<groupId>hibernate-conventions</groupId>
			<artifactId>hibernate-conventions</artifactId>
			<version>0.0.1-SNAPSHOT</version>
		</dependency>
	</dependency>
  
