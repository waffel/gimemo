/*
 * Copyright (c) 2015 Siemens GS IT EB CONF. All rights reserved.
 * This software is the confidential and proprietary information of Siemens GS IT EB CONF.
 * This file is part of SPICE.
 */
package org.waffel.gimemo.neuro.train;
import java.nio.file.Path;
/**
 * Created by Thomas Wabner on 16.06.2016.
 */
public class TrainResult {

  private boolean status;
  private Path savePath;

  public boolean isStatus() {
    return status;
  }
  public void setStatus(final boolean status) {
    this.status = status;
  }
  public Path getSavePath() {
    return savePath;
  }
  public void setSavePath(final Path savePath) {
    this.savePath = savePath;
  }
}
