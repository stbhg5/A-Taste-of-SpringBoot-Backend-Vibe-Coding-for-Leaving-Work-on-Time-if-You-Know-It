package com.example.todo.service;

import com.example.todo.dto.TodoDtos.TodoRequest;
import com.example.todo.dto.TodoDtos.TodoResponse;
import com.example.todo.entity.Todo;
import com.example.todo.entity.User;
import com.example.todo.exception.ResourceNotFoundException;
import com.example.todo.repository.TodoRepository;
import com.example.todo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public TodoService(TodoRepository todoRepository, UserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
    }

    public List<TodoResponse> findAll(Long userId) {
        List<Todo> todos = userId != null ? todoRepository.findByUserId(userId) : todoRepository.findAll();
        return todos.stream().map(TodoService::toResponse).toList();
    }

    public TodoResponse findById(Long id) {
        return toResponse(getTodoOrThrow(id));
    }

    @Transactional
    public TodoResponse create(TodoRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: id=" + request.userId()));
        Todo todo = new Todo();
        todo.setTitle(request.title());
        todo.setCompleted(request.completed() != null && request.completed());
        todo.setUser(user);
        return toResponse(todoRepository.save(todo));
    }

    @Transactional
    public TodoResponse update(Long id, TodoRequest request) {
        Todo todo = getTodoOrThrow(id);
        if (!todo.getUser().getId().equals(request.userId())) {
            User user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: id=" + request.userId()));
            todo.setUser(user);
        }
        todo.setTitle(request.title());
        todo.setCompleted(request.completed() != null && request.completed());
        return toResponse(todo);
    }

    @Transactional
    public void delete(Long id) {
        Todo todo = getTodoOrThrow(id);
        todoRepository.delete(todo);
    }

    public List<TodoResponse> findAllForUser(Long userId, String filter) {
        List<Todo> todos;
        if ("completed".equals(filter)) {
            todos = todoRepository.findByUserIdAndCompleted(userId, true);
        } else if ("incomplete".equals(filter)) {
            todos = todoRepository.findByUserIdAndCompleted(userId, false);
        } else {
            todos = todoRepository.findByUserId(userId);
        }
        return todos.stream().map(TodoService::toResponse).toList();
    }

    @Transactional
    public TodoResponse createForUser(Long userId, String title) {
        return create(new TodoRequest(title, false, userId));
    }

    @Transactional
    public TodoResponse toggleForUser(Long userId, Long todoId) {
        Todo todo = getOwnedTodoOrThrow(userId, todoId);
        todo.setCompleted(!todo.isCompleted());
        return toResponse(todo);
    }

    @Transactional
    public TodoResponse updateForUser(Long userId, Long todoId, String title) {
        Todo todo = getOwnedTodoOrThrow(userId, todoId);
        todo.setTitle(title);
        return toResponse(todo);
    }

    @Transactional
    public void deleteForUser(Long userId, Long todoId) {
        Todo todo = getOwnedTodoOrThrow(userId, todoId);
        todoRepository.delete(todo);
    }

    private Todo getOwnedTodoOrThrow(Long userId, Long todoId) {
        Todo todo = getTodoOrThrow(todoId);
        if (!todo.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("할 일을 찾을 수 없습니다: id=" + todoId);
        }
        return todo;
    }

    private Todo getTodoOrThrow(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("할 일을 찾을 수 없습니다: id=" + id));
    }

    private static TodoResponse toResponse(Todo todo) {
        return new TodoResponse(
                todo.getId(), todo.getTitle(), todo.isCompleted(),
                todo.getUser().getId(), todo.getCreatedAt(), todo.getUpdatedAt());
    }
}
