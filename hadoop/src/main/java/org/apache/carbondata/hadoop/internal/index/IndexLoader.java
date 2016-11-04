/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.carbondata.hadoop.internal.index;

import org.apache.carbondata.hadoop.internal.segment.Segment;
import org.apache.hadoop.conf.Configuration;

/**
 * Used to load the index
 */
public interface IndexLoader {

  /**
   * load the index based on the index information set in the configuration
   * @param segment the index from which segment to load
   * @return the loaded index
   */
  Index load(Segment segment);
}