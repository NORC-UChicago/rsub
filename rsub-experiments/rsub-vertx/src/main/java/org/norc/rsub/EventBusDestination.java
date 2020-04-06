package org.norc.rsub;

import com.sas.iom.SAS.ILanguageServicePackage.LineType;
import io.vertx.core.eventbus.EventBus;
import java.util.Arrays;

public class EventBusDestination implements IDestination {
  private EventBus eventBus;
  private String address;

  public EventBusDestination(EventBus eventBus, String address) {
    setOut(eventBus);
    this.address = address;
  }

  public void setOut(EventBus eventBus) {
    this.eventBus = eventBus;
  }

  @Override
  public void setOut(Object out) {
    setOut((EventBus) out);
  }

  @Override
  public void write(LineType[] lineTypes, String[] strings) {
    Arrays.asList(strings).forEach(line -> {
      eventBus.publish(address, line);
    });
  }

  @Override
  public void close() {
    //ignored
  }
}
