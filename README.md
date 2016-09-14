# Hibernate Conventions   [![Run Status](https://api.shippable.com/projects/540e74613479c5ea8f9e6238/badge?branch=master)](https://app.shippable.com/projects/540e74613479c5ea8f9e6238) [![Coverage Badge](https://api.shippable.com/projects/540e74613479c5ea8f9e6238/coverageBadge?branch=master)](https://app.shippable.com/projects/540e74613479c5ea8f9e6238)

A plugin for Hibernate thats allow validate, standardize e generate DDLs, SQLs and scripts in a simple way.

## Instalation

Just add the hibernate-conventions.jar on you classpath. It will automatically integrate with Hibernate.

### Gradle

    repositories {
        maven {
            url "https://bintray.com/andreluiznsilva/maven/hibernate-conventions"
        }
    }

    dependencies {
        compile 'hibernate-conventions:hibernate-conventions:1.0.0'       
    }
  
### Maven

	<repositories>
		<repository>
			<id>hibernate-conventions</id>
			<url>https://bintray.com/andreluiznsilva/maven/hibernate-conventions</url>
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
			<version>1.0.0</version>
		</dependency>
	</dependency>

## Validation

Allows apply a validation on Hibernate mappings e check the names of columns, tables and outher database objects. Can be customized using Hibernate property.

#### hibernate.conventions.maxLength (default = 255) 

Validate the max lenght of all generated database objects.

	<property name="hibernate.conventions.maxLength" value="30" />

## Conventions

Allows customize how Hibernate generate the DDLs, SQLs and scripts. Can be customized implementing hibernate.conventions.strategy.ConventionNamingStrategy interface.

#### hibernate.ejb.naming_strategy (default = hibernate.conventions.strategy.DefaultConventionNamingStrategy)

Can be used to configurate a ConventionNamingStrategy to generate all database objects.

	<property name="hibernate.ejb.naming_strategy" value="hibernate.conventions.dummy.TestConventionNamingStrategy" />
	
There are two implementations to match the Hibernate defaults implementations:

- hibernate.conventions.strategy.DefaultConventionNamingStrategy
- hibernate.conventions.strategy.ImprovedConventionNamingStrategy

## Setup

Tecnologies       | Version
------------------|--------------------
Java              | 5
Gradle            | 2.x
Hibernate         | 4.X
    
## Build
    
### Generate eclipse files

  gradle eclipse

### Compile

  gradle build
