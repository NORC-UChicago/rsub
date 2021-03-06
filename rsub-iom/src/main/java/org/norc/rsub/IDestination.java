package org.norc.rsub;

import com.sas.iom.SAS.ILanguageServicePackage.LineType;

public interface IDestination {
  void setOut(Object out);

  void write(LineType[] lineTypes, String[] strings);

  void close();
}
