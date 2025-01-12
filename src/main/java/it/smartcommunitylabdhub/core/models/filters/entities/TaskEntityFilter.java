package it.smartcommunitylabdhub.core.models.filters.entities;

import it.smartcommunitylabdhub.core.models.entities.task.TaskEntity;
import it.smartcommunitylabdhub.core.models.filters.interfaces.SpecificationFilter;
import it.smartcommunitylabdhub.core.utils.DateUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;


@Component
@Getter
@Setter
public class TaskEntityFilter extends BaseEntityFilter implements SpecificationFilter<TaskEntity> {

    private String function;

    @Override
    public Predicate toPredicate(Root<TaskEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Predicate predicate = criteriaBuilder.conjunction();

        if (function != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("function"), "%" + function + "%"));
        }

        if (getKind() != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("kind"), "%" + getKind() + "%"));
        }

        if (getState() != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("state"), "%" + getState() + "%"));
        }

        if (getCreatedDate() != null) {

            DateUtils.DateInterval dateInterval = DateUtils.parseDateIntervalFromTimestamps(getCreatedDate(), true);
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.between(root.get("created"), dateInterval.startDate(), dateInterval.endDate()));

        }

        return predicate;
    }
}
