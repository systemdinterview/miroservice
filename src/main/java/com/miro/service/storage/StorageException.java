package com.miro.service.storage;

public class StorageException extends RuntimeException{
	public StorageException() {
	}

	public StorageException(String msg) {
		super(msg);
	}

	public StorageException(String msg, Throwable t) {
		super(msg, t);
	}

}
