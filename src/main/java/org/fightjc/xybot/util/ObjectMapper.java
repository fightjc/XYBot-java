package org.fightjc.xybot.util;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ObjectMapper {

    private static ModelMapper modelMapper = new ModelMapper();

    /**
     * Model mapper property setting are specified in the following block.
     * Default property matching is set to Strict see {@link MatchingStrategies}
     * Custom mappings are added using {@link ModelMapper#addMappings(PropertyMap)}
     */
    static {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    /**
     * Hide from public usage.
     */
    private ObjectMapper() {}

    /**
     * <p>outClass object must have default constructor with no arguments.</p>
     * @param entity entity that needs to be mapped.
     * @param outClass class of result object.
     * @param <D> type of result object.
     * @param <T> type of source object to map from.
     * @return new object of <code>outClass</code> type.
     */
    public static <D, T> D map(final T entity, Class<D> outClass) {
        return modelMapper.map(entity, outClass);
    }

    /**
     * Maps {@code source} to {@code destination}.
     * @param source object to map from.
     * @param destination object to map to.
     * @param <S> type of source object to map from.
     * @param <D> type of result object.
     * @return
     */
    public static <S, D> D map(final S source, D destination) {
        modelMapper.map(source, destination);
        return destination;
    }

    /**
     * <p>Note: outClass object must have default constructor with no arguments.</p>
     * @param entityList list of entities that needs to be mapped.
     * @param outClass class of result list element.
     * @param <D> type of objects in result list.
     * @param <T> type of entity in <code>entityList</code> type.
     * @return list of mapped object with <code><D></code> type.
     */
    public static <D, T> List<D> mapAll(final Collection<T> entityList, Class<D> outClass) {
        return entityList.stream()
                .map(entity -> map(entity, outClass))
                .collect(Collectors.toList());
    }
}
