# Hibernate Conventions

[![Build Status](https://api.shippable.com/projects/540e74613479c5ea8f9e6238/badge?branchName=master)](https://app.shippable.com/projects/540e74613479c5ea8f9e6238/builds/latest)

Tecnologies       | Version
------------------|--------------------
Java              | 6
Gradle            | 2.x
HIbernate         | 4.X
    
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
  
