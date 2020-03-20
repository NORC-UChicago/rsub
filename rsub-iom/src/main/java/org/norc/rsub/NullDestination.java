package org.norc.rsub;

import com.sas.iom.SAS.ILanguageServicePackage.LineType;

public class NullDestination implements IDestination {
  @Override
  public void write(LineType[] lineTypes, String[] strings) {
    // ignored
  }

  @Override
  public void close() {
    // ignored
  }
}
