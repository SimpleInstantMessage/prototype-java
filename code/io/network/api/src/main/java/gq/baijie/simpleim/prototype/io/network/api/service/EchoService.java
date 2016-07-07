package gq.baijie.simpleim.prototype.io.network.api.service;

import gq.baijie.simpleim.prototype.business.api.Result;
import rx.Observable;

public interface EchoService {

  Observable<Result<byte[], Void>> echo(byte[] bytes);

}
