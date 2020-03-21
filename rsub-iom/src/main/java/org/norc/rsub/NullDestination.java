package org.norc.rsub;

import com.sas.iom.SAS.ILanguageServicePackage.LineType;

/** I do nothing, and that's the point. */
public class NullDestination implements IDestination {
  @Override
  public void setOut(Object out) {
    // ignored
  }

  @Override
  public void write(LineType[] lineTypes, String[] strings) {
    // ignored
  }

  @Override
  public void close() {
    // ignored
  }
}
