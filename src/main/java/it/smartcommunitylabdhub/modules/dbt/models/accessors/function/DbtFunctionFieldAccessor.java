package it.smartcommunitylabdhub.modules.dbt.models.accessors.function;

import it.smartcommunitylabdhub.core.annotations.common.AccessorType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.accessors.AbstractFieldAccessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.FunctionFieldAccessor;

@AccessorType(kind = "dbt", entity = EntityName.FUNCTION)
public class DbtFunctionFieldAccessor
        extends AbstractFieldAccessor<DbtFunctionFieldAccessor>
        implements FunctionFieldAccessor<DbtFunctionFieldAccessor> {

}
