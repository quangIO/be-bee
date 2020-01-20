package quang.to.bee.helper

import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.impl.DefaultConfiguration
import java.sql.DriverManager

object DatabaseHelper {
  init {
    Class.forName("org.postgresql.Driver")
  }

  private val dbUrl = System.getenv("JDBC_DATABASE_URL")
    ?: "jdbc:postgresql://localhost:5432/takehome?user=quangio"

  fun dslContext(): DSLContext = DSL.using(dbUrl)
  val configuration: Configuration =
    DefaultConfiguration().set(SQLDialect.POSTGRES).set(DriverManager.getConnection(dbUrl))
}
