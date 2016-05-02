/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.carbondata.integration.spark.testsuite.filterexpr

import java.io.File

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.CarbonContext


object FilterProcessor_FT {

  def main(args: Array[String]) {

    // get current directory:/examples
    val currentDirectory = new File(this.getClass.getResource("/").getPath + "/../../")
      .getCanonicalPath

    // specify parameters
    val storeLocation = currentDirectory + "/target/store"
    val kettleHome = new File(currentDirectory + "/../processing/carbonplugins").getCanonicalPath
    val hiveMetaPath = currentDirectory + "/target/hivemetadata"
    val testData = currentDirectory + "/src/main/resources/data.csv"

    val sc = new SparkContext(new SparkConf()
      .setAppName("CarbonExample")
      .setMaster("local")
    )

    val cc = new CarbonContext(sc, storeLocation)

    // As Carbon using kettle, so need to set kettle configuration
    cc.setConf("carbon.kettle.home", kettleHome)
    cc.setConf("hive.metastore.warehouse.dir", hiveMetaPath)

    // whether use table split partition
    // true -> use table split partition, support multiple partition loading
    // false -> use node split partition, support data load by host partition
   // CarbonProperties.getInstance().addProperty("carbon.table.split.partition.enable", "false")

     cc.sql("drop cube if exists filtertestTable")
     cc.sql("CREATE CUBE filtertestTable DIMENSIONS (ID Integer, date Timestamp, country String, " +
        "name String, phonetype String, serialname String) " +
        "MEASURES (salary Integer) " +
        "OPTIONS (PARTITIONER [PARTITION_COUNT=1])"
      )
    cc.sql(
      s"LOAD DATA FACT FROM '$testData' INTO CUBE filtertestTable OPTIONS(DELIMITER ',', " +
        s"FILEHEADER '')"
    )

    // Include query

    cc.sql("select Country from filtertestTable " + "where Country ='china'").show()
    // Exclude Query

    cc.sql("select Country from filtertestTable " + "where Country !='china'").show()
    // In query

    cc.sql("select Country,count(salary) from filtertestTable " +
      "where Country in ('china','france') group by Country"
    )
      .show()
    // Is not null query

    cc.sql("select Country from filtertestTable where Country is not null").show()
    // Is null query which goes to Row level executer for execution

    cc.sql("select Country from filtertestTable where Country is null").show()
    // Filter with order by query which was failing.

    cc.sql("select id from filtertestTable " +
      "where Country in ('china','france') order by id"
    )
      .show()

  }
}