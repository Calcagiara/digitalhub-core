package it.smartcommunitylabdhub.core.models.accessors.kinds.functions;

import it.smartcommunitylabdhub.core.annotations.common.AccessorType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.accessors.kinds.abstracts.AbstractFieldAccessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.FunctionFieldAccessor;

@AccessorType(kind = "dbt", entity = EntityName.FUNCTION)
public class DbtFunctionFieldAccessor
        extends AbstractFieldAccessor<DbtFunctionFieldAccessor>
        implements FunctionFieldAccessor<DbtFunctionFieldAccessor> {

}
