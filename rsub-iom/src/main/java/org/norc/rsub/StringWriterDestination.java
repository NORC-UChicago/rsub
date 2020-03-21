package org.norc.rsub;

import com.sas.iom.SAS.ILanguageServicePackage.LineType;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

public class StringWriterDestination implements IDestination {
  private StringWriter writer;

  public StringWriterDestination() {}

  public StringWriterDestination(StringWriter writer) {
    setOut(writer);
  }

  @Override
  public void setOut(Object out) {
    setOut((StringWriter) out);
  }

  public void setOut(StringWriter writer) {
    this.writer = writer;
  }

  @Override
  public void write(LineType[] lineTypes, String[] strings) {
    Arrays.stream(strings).forEachOrdered(writer::write);
    writer.flush();
  }

  @Override
  public void close() {
    try {
      writer.close();
    } catch (IOException err) {
      throw new RuntimeException(err.getMessage());
    }
  }
}
