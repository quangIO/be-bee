package quang.to.bee

import jooq.codegen.tables.daos.HoneyEventDao
import jooq.codegen.tables.daos.NectarEventDao
import jooq.codegen.tables.daos.BeeStatusDao
import jooq.codegen.tables.daos.StatusEventDao
import quang.to.bee.helper.DatabaseHelper

object MappedDb {
  val statusEventDao = StatusEventDao(DatabaseHelper.configuration)
  val statusDao = BeeStatusDao(DatabaseHelper.configuration)
  val nectarDao = NectarEventDao(DatabaseHelper.configuration)
  val honeyDao = HoneyEventDao(DatabaseHelper.configuration)
}
