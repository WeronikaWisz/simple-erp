package com.simpleerp.simpleerpapp.services;

import com.simpleerp.simpleerpapp.dtos.manageusers.UserName;
import com.simpleerp.simpleerpapp.dtos.trade.UpdateAssignedUserRequest;
import com.simpleerp.simpleerpapp.dtos.warehouse.DelegatedTaskListItem;
import com.simpleerp.simpleerpapp.dtos.warehouse.DelegatedTasksResponse;
import com.simpleerp.simpleerpapp.enums.EDirection;
import com.simpleerp.simpleerpapp.enums.ERole;
import com.simpleerp.simpleerpapp.enums.EStatus;
import com.simpleerp.simpleerpapp.enums.ETask;
import com.simpleerp.simpleerpapp.exception.ApiNotFoundException;
import com.simpleerp.simpleerpapp.models.*;
import com.simpleerp.simpleerpapp.repositories.*;
import com.simpleerp.simpleerpapp.security.userdetails.UserDetailsI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductionService {

    private static final String RELEASE_PREFIX = "WW";
    private static final String ACCEPTANCE_PREFIX = "PW";
    private static final String SEPARATOR = "/";

    private final ProductRepository productRepository;
    private final ProductSetRepository productSetRepository;
    private final ProductionRepository productionRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ReleaseRepository releaseRepository;
    private final AcceptanceRepository acceptanceRepository;
    private final ProductProductionRepository productProductionRepository;
    private final MessageSource messageSource;

    @Autowired
    public ProductionService(ProductRepository productRepository, ProductSetRepository productSetRepository,
                             ProductionRepository productionRepository, UserRepository userRepository,
                             TaskRepository taskRepository, ReleaseRepository releaseRepository,
                             AcceptanceRepository acceptanceRepository, MessageSource messageSource,
                             ProductProductionRepository productProductionRepository) {
        this.productRepository = productRepository;
        this.productSetRepository = productSetRepository;
        this.productionRepository = productionRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.releaseRepository = releaseRepository;
        this.acceptanceRepository = acceptanceRepository;
        this.productProductionRepository = productProductionRepository;
        this.messageSource = messageSource;
    }

    public DelegatedTasksResponse loadProductionTasks(EStatus status, int page, int size) {
        DelegatedTasksResponse delegatedTasksResponse = new DelegatedTasksResponse();
        List<Production> productionList = productionRepository.findByStatus(status)
                .orElse(Collections.emptyList());
        int total = productionList.size();
        int start = page * size;
        int end = Math.min(start + size, total);
        if(end >= start) {
            delegatedTasksResponse.setTasksList(productionTaskListToDelegatedTasksListItem(productionList
                    .stream().sorted(Comparator.comparing(Production::getCreationDate)).collect(Collectors.toList())
                    .subList(start, end)));
        }
        delegatedTasksResponse.setTotalTasksLength(total);
        return delegatedTasksResponse;
    }

    private List<DelegatedTaskListItem> productionTaskListToDelegatedTasksListItem(List<Production> productionList){
        List<DelegatedTaskListItem> delegatedTaskListItems = new ArrayList<>();
        for(Production production : productionList){

            String orderNumber = "";
            boolean accepted = false;
            if(production.getStatus().equals(EStatus.WAITING)) {
                Optional<Release> release = releaseRepository.findByProduction(production);
                if (release.isPresent()) {
                    orderNumber = release.get().getNumber();
                    accepted = release.get().getStatus().equals(EStatus.DONE);
                }
            } else if (production.getStatus().equals(EStatus.IN_PROGRESS)){
                Optional<Acceptance> acceptance = acceptanceRepository.findByProduction(production);
                if (acceptance.isPresent()) {
                    orderNumber = acceptance.get().getNumber();
                    accepted = acceptance.get().getStatus().equals(EStatus.DONE);
                }
            }

            DelegatedTaskListItem delegatedTaskListItem = new DelegatedTaskListItem(production.getId(),
                    production.getNumber(), production.getProduct().getCode(), production.getProduct().getName(),
                    production.getProduct().getUnit(), production.getQuantity().toString(), production.getStatus(),
                    production.getRequestingUser().getName() + " " +  production.getRequestingUser().getSurname(),
                    production.getRequestingUser().getId(),
                    production.getAssignedUser().getName() + " " +  production.getAssignedUser().getSurname(),
                    production.getAssignedUser().getId(),
                    production.getCreationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    orderNumber, accepted);
            delegatedTaskListItems.add(delegatedTaskListItem);
        }
        return delegatedTaskListItems;
    }

    @Transactional
    public void markProductionAsInProgress(List<Long> ids) {
        for(Long id: ids){
            Production production = productionRepository.findById(id)
                    .orElseThrow(() -> new ApiNotFoundException("exception.productionNotFound"));
            production.setStatus(EStatus.IN_PROGRESS);
            production.setUpdateDate(LocalDateTime.now());
            productionRepository.save(production);
        }
    }

    @Transactional
    public void markProductionAsDone(List<Long> ids) {
        for(Long id: ids){
            Production production = productionRepository.findById(id)
                    .orElseThrow(() -> new ApiNotFoundException("exception.productionNotFound"));
            production.setStatus(EStatus.DONE);
            LocalDateTime currentDate = LocalDateTime.now();
            production.setUpdateDate(currentDate);
            production.setExecutionDate(currentDate);
            productionRepository.save(production);
        }
    }

    @Transactional
    public void delegateInternalRelease(List<Long> ids) {
        for(Long id: ids){
            Production production = productionRepository.findById(id)
                    .orElseThrow(() -> new ApiNotFoundException("exception.productionNotFound"));
            this.createInternalRelease(production);
            production.setUpdateDate(LocalDateTime.now());
            productionRepository.save(production);
        }
    }

    private void createInternalRelease(Production production) {
        Task task = taskRepository.findByName(ETask.TASK_INTERNAL_RELEASE)
                .orElseThrow(() -> new ApiNotFoundException("exception.taskNotFound"));

        String username = getCurrentUserUsername();
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Cannot found user"));

        LocalDateTime currentDate = LocalDateTime.now();

        Release release = releaseRepository.save(new Release(production, user, task.getDefaultUser(),
                EDirection.INTERNAL, currentDate, EStatus.WAITING));

        String releaseNumber = RELEASE_PREFIX + SEPARATOR + currentDate.getDayOfMonth()
                + SEPARATOR + currentDate.getMonthValue()
                + SEPARATOR + currentDate.getYear()
                + SEPARATOR + release.getId();

        release.setNumber(releaseNumber);
        releaseRepository.save(release);
    }

    @Transactional
    public void delegateInternalAcceptance(List<Long> ids) {
        for(Long id: ids){
            Production production = productionRepository.findById(id)
                    .orElseThrow(() -> new ApiNotFoundException("exception.productionNotFound"));
            this.createInternalAcceptance(production);
            production.setUpdateDate(LocalDateTime.now());
            productionRepository.save(production);
        }
    }

    private void createInternalAcceptance(Production production) {
        Task task = taskRepository.findByName(ETask.TASK_INTERNAL_ACCEPTANCE)
                .orElseThrow(() -> new ApiNotFoundException("exception.taskNotFound"));

        String username = getCurrentUserUsername();
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Cannot found user"));

        LocalDateTime currentDate = LocalDateTime.now();

        Acceptance acceptance = acceptanceRepository.save(new Acceptance(production, user,
                task.getDefaultUser(), EDirection.INTERNAL, currentDate, EStatus.WAITING));

        String acceptanceNumber = ACCEPTANCE_PREFIX + SEPARATOR + currentDate.getDayOfMonth()
                + SEPARATOR + currentDate.getMonthValue()
                + SEPARATOR + currentDate.getYear()
                + SEPARATOR + acceptance.getId();
        acceptance.setNumber(acceptanceNumber);
        acceptanceRepository.save(acceptance);

    }

    public List<UserName> loadUsers() {
        List<User> userList = userRepository.findAll()
                .stream().filter(user -> user.getRoles().stream().map(Role::getName).collect(Collectors.toList())
                        .contains(ERole.ROLE_PRODUCTION)).collect(Collectors.toList());
        Optional<User> admin = userRepository.findByUsername("admin");
        admin.ifPresent(userList::add);
        List<UserName> userNameList = new ArrayList<>();
        for (User user: userList){
            userNameList.add(new UserName(user.getId(), user.getName() + " " + user.getSurname()));
        }
        return userNameList;
    }

    @Transactional
    public void updateAssignedUser(UpdateAssignedUserRequest updateAssignedUserRequest) {
        User user = userRepository.findById(updateAssignedUserRequest.getEmployeeId())
                .orElseThrow(() -> new ApiNotFoundException("exception.userNotFound"));
        if(updateAssignedUserRequest.getTask().equals(ETask.TASK_PRODUCTION)) {
            for (Long id : updateAssignedUserRequest.getTaskIds()) {
                Production production = productionRepository.findById(id)
                        .orElseThrow(() -> new ApiNotFoundException("exception.productionNotFound"));
                production.setAssignedUser(user);
                production.setUpdateDate(LocalDateTime.now());
                productionRepository.save(production);
            }
        }
    }

    private String getCurrentUserUsername(){
        UserDetailsI userDetails = (UserDetailsI) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return userDetails.getUsername();
    }
}
