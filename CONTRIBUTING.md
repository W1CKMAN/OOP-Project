# Contributing to CarCare Pro

First off, thank you for considering contributing to CarCare Pro! 🎉 It's people like you that make CarCare Pro such a great tool.

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [How Can I Contribute?](#how-can-i-contribute)
- [Style Guidelines](#style-guidelines)
- [Commit Messages](#commit-messages)
- [Pull Request Process](#pull-request-process)

## 📜 Code of Conduct

This project and everyone participating in it is governed by our Code of Conduct. By participating, you are expected to uphold this code. Please report unacceptable behavior to [your-email@example.com].

### Our Standards

- Using welcoming and inclusive language
- Being respectful of differing viewpoints and experiences
- Gracefully accepting constructive criticism
- Focusing on what is best for the community
- Showing empathy towards other community members

## 🚀 Getting Started

### Prerequisites

Before you begin, ensure you have the following installed:
- Java 21 or higher
- Maven 3.8+
- MySQL 8.0+
- Git

### Setting Up the Development Environment

1. **Fork the repository**
   
   Click the "Fork" button at the top right of the repository page.

2. **Clone your fork**
   ```bash
   git clone https://github.com/YOUR-USERNAME/OOP-Project.git
   cd OOP-Project
   ```

3. **Add the upstream remote**
   ```bash
   git remote add upstream https://github.com/ORIGINAL-OWNER/OOP-Project.git
   ```

4. **Install dependencies**
   ```bash
   mvn clean install
   ```

5. **Set up the database**
   - Create a MySQL database named `carcare`
   - Run the schema from `database_schema.sql`
   - Configure `src/main/resources/config.properties`

6. **Run the application**
   ```bash
   mvn exec:java -Dexec.mainClass="Main.Main"
   ```

## 🤝 How Can I Contribute?

### 🐛 Reporting Bugs

Before creating bug reports, please check existing issues to avoid duplicates.

**When reporting a bug, include:**

- **Clear title** describing the issue
- **Steps to reproduce** the behavior
- **Expected behavior** vs **actual behavior**
- **Screenshots** if applicable
- **Environment details:**
  - OS and version
  - Java version (`java -version`)
  - Maven version (`mvn -version`)
  - MySQL version

**Bug Report Template:**
```markdown
## Bug Description
A clear and concise description of what the bug is.

## Steps to Reproduce
1. Go to '...'
2. Click on '....'
3. Scroll down to '....'
4. See error

## Expected Behavior
What you expected to happen.

## Screenshots
If applicable, add screenshots.

## Environment
- OS: [e.g., Windows 11, macOS Ventura]
- Java Version: [e.g., 21.0.1]
- MySQL Version: [e.g., 8.0.35]
```

### 💡 Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues.

**When suggesting an enhancement, include:**

- **Clear title** describing the feature
- **Detailed description** of the proposed functionality
- **Use case** - why is this feature needed?
- **Mockups/wireframes** if applicable
- **Possible implementation** approach (optional)

### 🔧 Code Contributions

#### Types of Contributions We Welcome

- 🐛 Bug fixes
- ✨ New features
- 📝 Documentation improvements
- 🎨 UI/UX enhancements
- ⚡ Performance improvements
- ✅ Test coverage improvements
- 🔒 Security enhancements

#### First Time Contributors

Look for issues labeled:
- `good first issue` - Good for newcomers
- `help wanted` - Extra attention is needed
- `documentation` - Documentation improvements

## 📏 Style Guidelines

### Java Code Style

We follow standard Java conventions with some modifications:

```java
// ✅ Good
public class CustomerService {
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);
    
    private final CustomerDAO customerDAO;
    
    public CustomerService() {
        this.customerDAO = new CustomerDAOImpl();
    }
    
    public Optional<Customer> findById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be positive");
        }
        return customerDAO.findById(id);
    }
}

// ❌ Bad
public class customerService {
    CustomerDAO dao;
    public customerService() { dao = new CustomerDAOImpl(); }
    public Customer findById(int id) { return dao.findById(id).get(); }
}
```

### Code Style Rules

| Rule | Example |
|------|---------|
| Class names | `PascalCase` - `CustomerService` |
| Method names | `camelCase` - `findById()` |
| Constants | `UPPER_SNAKE_CASE` - `MAX_CONNECTIONS` |
| Variables | `camelCase` - `customerList` |
| Packages | `lowercase` - `services` |
| Braces | Same line for classes/methods |
| Indentation | 4 spaces (no tabs) |
| Line length | Max 120 characters |

### Documentation Standards

- All public methods must have Javadoc
- Include `@param`, `@return`, and `@throws` where applicable
- Write meaningful comments for complex logic

```java
/**
 * Finds a customer by their unique identifier.
 *
 * @param id the unique identifier of the customer (must be positive)
 * @return an Optional containing the customer if found, empty otherwise
 * @throws IllegalArgumentException if id is not positive
 */
public Optional<Customer> findById(int id) {
    // Implementation
}
```

### UI/Swing Guidelines

- Use MigLayout for all layouts
- Follow FlatLaf theming conventions
- Ensure components are accessible
- Test on multiple screen resolutions

```java
// ✅ Preferred layout approach
JPanel panel = new JPanel(new MigLayout("fill, insets 20", "[grow]", "[]10[]"));
panel.add(titleLabel, "wrap");
panel.add(contentPanel, "grow");
```

## 💬 Commit Messages

We follow [Conventional Commits](https://www.conventionalcommits.org/):

### Format
```
<type>(<scope>): <description>

[optional body]

[optional footer(s)]
```

### Types

| Type | Description |
|------|-------------|
| `feat` | New feature |
| `fix` | Bug fix |
| `docs` | Documentation only |
| `style` | Code style (formatting, etc.) |
| `refactor` | Code refactoring |
| `perf` | Performance improvement |
| `test` | Adding/updating tests |
| `chore` | Maintenance tasks |
| `build` | Build system changes |
| `ci` | CI configuration |

### Examples

```bash
# Feature
feat(customer): add customer search functionality

# Bug fix
fix(order): resolve null pointer in order calculation

# Documentation
docs(readme): update installation instructions

# Refactoring
refactor(dao): extract common query logic to base class
```

## 🔀 Pull Request Process

### Before Submitting

1. **Ensure your code compiles**
   ```bash
   mvn clean compile
   ```

2. **Run tests** (when available)
   ```bash
   mvn test
   ```

3. **Update documentation** if needed

4. **Sync with upstream**
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

### Submitting a Pull Request

1. **Create a branch** from `main`
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes** following our style guidelines

3. **Commit your changes** using conventional commits

4. **Push to your fork**
   ```bash
   git push origin feature/your-feature-name
   ```

5. **Open a Pull Request** with:
   - Clear title following conventional commit format
   - Description of changes
   - Related issue number(s)
   - Screenshots for UI changes

### PR Template

```markdown
## Description
Brief description of changes.

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Related Issues
Fixes #(issue number)

## Testing
Describe how you tested your changes.

## Screenshots (if applicable)
Add screenshots here.

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-reviewed my code
- [ ] Added necessary documentation
- [ ] No new warnings introduced
```

### Review Process

1. **Automated checks** must pass
2. **At least one approval** from maintainers
3. **No unresolved conversations**
4. **Up-to-date with main branch**

## 🏷️ Issue Labels

| Label | Description |
|-------|-------------|
| `bug` | Something isn't working |
| `enhancement` | New feature or request |
| `documentation` | Documentation improvements |
| `good first issue` | Good for newcomers |
| `help wanted` | Extra attention needed |
| `priority: high` | High priority |
| `priority: low` | Low priority |
| `wontfix` | Won't be worked on |
| `duplicate` | Duplicate issue |

## 📞 Getting Help

- 💬 Open a [Discussion](../../discussions) for questions
- 📧 Email: [your-email@example.com]
- 📖 Check the [Wiki](../../wiki) for guides

---

## 🙏 Thank You!

Your contributions make CarCare Pro better for everyone. We appreciate your time and effort!

<p align="center">
  <strong>Happy Coding! 💻✨</strong>
</p>
