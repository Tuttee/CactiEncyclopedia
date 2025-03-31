# Getting Started with Cacti Encyclopedia

## **1. Setting Up Kafka**
Start **apache/kafka:3.9.0** as a Docker container. Ensure it's running properly before starting the application.

## **2. Configure Kafka in the Application**
Edit **`application-dev.properties`** and set:
```properties
spring.kafka.bootstrap-servers=<your_kafka_address>
```

## **3. Configure Database Credentials**
Choose one of the following methods:
- **Option A:** Edit `application-dev.properties`:
  ```properties
  spring.datasource.username=<your_user>
  spring.datasource.password=<your_password>
  ```
- **Option B:** Use environment variables:
  ```bash
  export MYSQL_USER=<your_user>
  export MYSQL_PASS=<your_password>
  ```

## **4. Set Active Profile to `dev`**
Ensure the application is running with the **`dev`** profile.

## **5. Initial Start Without `data.sql` Import**
Run the application **without importing** `data.sql` so the database can initialize.

## **6. Import `data.sql` After First Start**
After the first successful start:
1. **Enable SQL import** by adding this to `application.properties`:
   ```properties
   spring.sql.init.mode=always
   ```
2. Restart the application to execute the import.
3. After import, **disable SQL import** by changing `always` to `never`:
   ```properties
   spring.sql.init.mode=never
   ```

## **7. User Roles**
- The **first registered user** gets **ADMIN** role.
- Every following user gets the **USER** role.

## **8. Configure Facts Service**
Set up the **Facts Service URL** in `application.properties`:
```properties
facts-svc.base-url=<your_facts_service_url>
```

---

# **Running Cacti Encyclopedia with Docker**

## **1. Clone the Required Repositories**
```bash
git clone https://github.com/Tuttee/CactiEncyclopedia.git
git clone https://github.com/Tuttee/facts-svc.git
```
Ensure both repos are in adjacent directories.

## **2. Build the Applications**
Inside each repo, run:
```bash
mvn clean install
```

## **3. Start with Docker**
From the **Cacti Encyclopedia** repo, run:
```bash
docker compose up -d
```
> **Note:** The `facts-svc` **build** property in `docker-compose.yml` may need directory adjustments.

---

## 🎉 **Cacti Encyclopedia is Now Running!**
You can access it and start using the service. Let me know if you need any help! 🚀
