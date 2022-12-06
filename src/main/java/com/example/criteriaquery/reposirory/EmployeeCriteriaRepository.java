package com.example.criteriaquery.reposirory;


import com.example.criteriaquery.model.Employee;
import com.example.criteriaquery.model.EmployeePage;
import com.example.criteriaquery.model.EmployeeSearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository

public class EmployeeCriteriaRepository {

    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    public EmployeeCriteriaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public Page<Employee> findAllFilters(EmployeePage employeePage, EmployeeSearchCriteria employeeSearchCriteria) {

        CriteriaQuery<Employee> criteriaQuery = criteriaBuilder.createQuery(Employee.class);
        Root<Employee> employeeRoot = criteriaQuery.from(Employee.class);

        Predicate predicate = getPredicate(employeeSearchCriteria, employeeRoot);
        setOrder(employeePage, criteriaQuery, employeeRoot);

        TypedQuery<Employee> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(employeePage.getPageNumber() * employeePage.getPageSize());
        typedQuery.setMaxResults(employeePage.getPageSize());

        Pageable pageable = getPageable(employeePage);

        long employeesCount = getEmployeesCount(predicate);

        return new PageImpl<>(typedQuery.getResultList(), pageable, employeesCount);

    }


    private Predicate getPredicate(EmployeeSearchCriteria employeeSearchCriteria, Root<Employee> employeeRoot) {
        List<Predicate> predicates = new ArrayList<>();
        if (Objects.nonNull(employeeSearchCriteria.getFirstName())) {
            predicates.add(
                    criteriaBuilder.like(employeeRoot.get("firstName"), "%" + employeeSearchCriteria.getFirstName() + "%")
            );
        }
        if (Objects.nonNull(employeeSearchCriteria.getLastName())) {
            predicates.add(
                    criteriaBuilder.like(employeeRoot.get("lastName"), "%" + employeeSearchCriteria.getLastName() + "%")
            );
        }
        return criteriaBuilder.and(predicates.toArray(predicates.toArray(new Predicate[0])));
    }


    private void setOrder(EmployeePage employeePage,
                          CriteriaQuery<Employee> criteriaQuery,
                          Root<Employee> employeeRoot) {

        if (employeePage.getSortDirection().equals(Sort.Direction.ASC)) {
            criteriaQuery.orderBy(criteriaBuilder.asc(employeeRoot.get(employeePage.getSortBy())));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(employeeRoot.get(employeePage.getSortBy())));
        }


    }

    private Pageable getPageable(EmployeePage employeePage) {
        Sort sort = Sort.by(employeePage.getSortDirection(), employeePage.getSortBy());
        return PageRequest.of(employeePage.getPageNumber(), employeePage.getPageSize(), sort);
    }

    private long getEmployeesCount(Predicate predicate) {
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Employee> countRoot = criteriaQuery.from(Employee.class);
        criteriaQuery.select(criteriaBuilder.count(countRoot)).where(predicate);
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }


}
