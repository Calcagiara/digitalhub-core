package it.smartcommunitylabdhub.core.models.builders;

import java.util.function.Consumer;
import java.util.function.Supplier;
import it.smartcommunitylabdhub.core.models.base.interfaces.BaseEntity;

public class EntityFactory {

    public static <T extends BaseEntity, U extends BaseEntity> T create(Supplier<T> entitySupplier,
            U entity,
            Consumer<EntityBuilder<T, U>> buildFunction) {
        EntityBuilder<T, U> builder = new EntityBuilder<>(entitySupplier);
        buildFunction.accept(builder);
        return builder.build();
    }

    public static <T extends BaseEntity, U extends BaseEntity> T combine(T sourceEntity, U entity,
            Consumer<EntityBuilder<T, U>> buildFunction) {
        Supplier<T> entitySupplier = () -> sourceEntity;
        return create(entitySupplier, entity, buildFunction);
    }
}
