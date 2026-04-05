package com.ecommerce.util;

public class Constants {

    public static final class Validation {
        public static final int MIN_PASSWORD_LENGTH = 8;
        public static final int MAX_PASSWORD_LENGTH = 128;
        public static final int MIN_NAME_LENGTH = 2;
        public static final int MAX_NAME_LENGTH = 100;
        public static final int MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    }

    public static final class Pagination {
        public static final int DEFAULT_PAGE = 0;
        public static final int DEFAULT_SIZE = 20;
        public static final int MAX_SIZE = 100;
    }

    public static final class Cache {
        public static final String CATEGORIES = "categories";
        public static final String PRODUCTS = "products";
        public static final long CACHE_EXPIRATION_MINUTES = 30;
    }

    public static final class File {
        public static final String UPLOAD_DIR = "storage/uploads";
        public static final String ALLOWED_EXTENSIONS = "jpg,jpeg,png,webp,gif";
    }
}