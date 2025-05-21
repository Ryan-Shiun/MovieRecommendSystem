package model.javaBean;

import java.time.LocalDateTime;

public record AppUser(
		int userId,
		String username,
		String email,
		String password,
		LocalDateTime createdAt
) {}
