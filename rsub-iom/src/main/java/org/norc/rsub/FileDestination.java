package org.norc.rsub;

import com.sas.iom.SAS.ILanguageServicePackage.LineType;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileDestination implements IDestination {
  private FileWriter writer;
  private BufferedWriter buffer;

  public FileDestination() {}

  public FileDestination(File destination) {
    setOut(destination);
  }

  public void setOut(File out) {
    try {
      writer = new FileWriter((File) out);
      buffer = new BufferedWriter(writer);
    } catch (IOException err) {
      throw new RuntimeException(err.getMessage());
    }
  }

  @Override
  public void setOut(Object out) {
    setOut((File) out);
  }

  @Override
  public void write(LineType[] lineTypes, String[] strings) {
    try {
      for (String string : strings) {
        buffer.write(string + System.lineSeparator());
      }
      buffer.flush();
    } catch (IOException err) {
      throw new RuntimeException(err.getMessage());
    }
  }

  @Override
  public void close() {
    try {
      buffer.close();
      writer.close();
    } catch (IOException err) {
      throw new RuntimeException(err.getMessage());
    }
  }
}
