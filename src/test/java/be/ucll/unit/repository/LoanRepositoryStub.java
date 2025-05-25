package be.ucll.unit.repository;

import be.ucll.model.Loan;
import be.ucll.repository.LoanRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LoanRepositoryStub implements LoanRepository {

    private List<Loan> loans;

    public LoanRepositoryStub() {
        loans = new ArrayList<>();
    }

    public List<Loan> getAllLoans() {
        return loans;
    }

    public List<Loan> findLoanByUser(String email, boolean onlyActive) {
        return loans.stream()
                .filter(loan -> loan.getUser().getEmail().equals(email))
                .filter(loan -> {
                    if (onlyActive) {
                        return !loan.isReturned() && !loan.getEndDate().isBefore(LocalDate.now());
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    public void deleteUserLoans(String email) {
        List<Loan> userLoans = findLoanByUser(email, false);
        loans.removeAll(userLoans);
    }

    @Override
    public List<Loan> findAllByUserEmailAndIsReturnedFalseAndEndDateAfter(String email, LocalDate now) {
        return loans.stream()
                .filter(loan -> loan.getUser().getEmail().equals(email))
                .filter(loan -> !loan.isReturned() && loan.getEndDate() != null && loan.getEndDate().isAfter(now))
                .collect(Collectors.toList());
    }

    @Override
    public Loan findLoanByUserEmailAndEndDateAfter(String userEmail, LocalDate endDateAfter) {
        return loans.stream()
                .filter(loan -> loan.getUser().getEmail().equals(userEmail))
                .filter(loan -> loan.getEndDate() != null && loan.getEndDate().isAfter(endDateAfter))
                .findFirst()
                .orElse(null);
    }


    @Override
    public List<Loan> findAllByUserEmailAndIsReturnedFalse(String userEmail) {
        return loans.stream()
                .filter(loan -> loan.getUser().getEmail().equals(userEmail))
                .filter(loan -> !loan.isReturned() && loan.getEndDate() != null)
                .collect(Collectors.toList());
    }

    @Override
    public List<Loan> findAllByUserEmail(String email) {
        return loans.stream()
                .filter(loan -> loan.getUser().getEmail().equals(email))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllByUserEmail(String email) {
        loans.removeIf(loan -> loan.getUser().getEmail().equals(email));
    }

    @Override
    public <S extends Loan> S save(S loan) {
        loans.add(loan);
        return loan;
    }



    @Override
    public void flush() {

    }

    @Override
    public <S extends Loan> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Loan> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<Loan> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {
    }

    @Override
    public Loan getOne(Long aLong) {
        return null;
    }

    @Override
    public Loan getById(Long aLong) {
        return null;
    }

    @Override
    public Loan getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends Loan> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Loan> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends Loan> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Loan> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Loan> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Loan> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Loan, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }


    @Override
    public <S extends Loan> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<Loan> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<Loan> findAll() {
        return List.of();
    }

    @Override
    public List<Loan> findAllById(Iterable<Long> longs) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(Loan entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Loan> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Loan> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<Loan> findAll(Pageable pageable) {
        return null;
    }


}