package io.github.thiagolvlsantos.file.rest.storage.rest;

import java.util.List;

public interface IObjectMapper {

	<P, Q> Q map(P source, Class<Q> type);

	<P, Q> List<Q> mapList(Iterable<P> source, Class<Q> type);
}
