package gq.baijie.simpleim.prototype.server.impl.vertx.codec;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class CodecModule {

  @Provides
  @Singleton
  RecordCodec provideRecordCodec() {
    RecordCodec recordCodec = new RecordCodec();
    recordCodec.registerRecordDataCodec(new AccountServerRequestCodec());
    recordCodec.registerRecordDataCodec(new AccountServerResponseCodec());
    recordCodec.registerRecordDataCodec(new MessageCodec());
    return recordCodec;
  }

}
