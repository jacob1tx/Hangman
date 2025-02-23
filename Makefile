SRC_DIR = src
BIN_DIR = bin
LIB_DIR = EOWL-v1.1.2
JAR_NAME = Hangman.jar
MAIN_CLASS = Main
CLASSPATH = $(LIB_DIR)/*:$(BIN_DIR)

# List of all source files
SRC_FILES = $(wildcard $(SRC_DIR)/*.java)
CLASS_FILES = $(patsubst $(SRC_DIR)/%.java, $(BIN_DIR)/$(SRC_DIR)/%.class, $(SRC_FILES))

# Ensure bin directory exists before compilation
$(BIN_DIR):
	mkdir -p $(BIN_DIR)

# Compile Java source files into class files
$(BIN_DIR)/$(SRC_DIR)/%.class: $(SRC_DIR)/%.java | $(BIN_DIR)
	javac -d $(BIN_DIR) $<

# Default target
all: compile

# Compile all source files
compile: $(CLASS_FILES)

# Run the application (adjusting for directory structure)
run: compile
	java -cp $(BIN_DIR) $(SRC_DIR).$(MAIN_CLASS)

# Package the compiled files into a JAR
package: compile
	jar cfe $(JAR_NAME) $(SRC_DIR).$(MAIN_CLASS) -C $(BIN_DIR) .

# Clean up class files and JAR
clean:
	rm -rf $(BIN_DIR)/$(SRC_DIR)/*.class $(JAR_NAME)

# Clean, compile, and package
clean-package: clean compile package