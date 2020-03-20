package org.norc.rsub;

import com.sas.iom.SAS.ILanguageServicePackage.LineType;

public interface IDestination {
  void write(LineType[] lineTypes, String[] strings);

  void close();
}
