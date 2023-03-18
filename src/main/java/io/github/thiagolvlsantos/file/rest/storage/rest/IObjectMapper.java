package io.github.thiagolvlsantos.file.rest.storage.rest;

import java.util.List;

public interface IObjectMapper {

	<Q> Q read(String content, Class<Q> type);

	<P, Q> Q map(P source, Class<Q> type);

	<P, Q> List<Q> mapList(Iterable<P> source, Class<Q> type);
}
