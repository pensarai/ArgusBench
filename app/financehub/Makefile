.PHONY: build run test clean compose-up compose-down

build:
	mvn -f app/financehub/pom.xml -B -ntp -DskipTests package

run:
	mvn -f app/financehub/pom.xml -B -ntp spring-boot:run

test:
	mvn -f app/financehub/pom.xml -B -ntp verify

compose-up:
	docker compose -f app/financehub/docker-compose.yml up -d --build

compose-down:
	docker compose -f app/financehub/docker-compose.yml down -v

clean:
	mvn -f app/financehub/pom.xml -B -ntp clean


