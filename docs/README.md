# PSP Service Documentation

This directory contains all documentation for the PSP (Payment Service Provider) service.

## Language Requirement

**ALL documentation in this directory MUST be written in English.**

This is a project rule that applies to:
- All markdown files
- All text files
- All documentation formats
- Code examples and comments
- API documentation
- Technical specifications

## Directory Structure

### Internal Documentation (`internal/`)
- **api/** — Internal API specifications and contracts
- **design/** — Architecture and system design documents
- **security/** — Security policies and procedures
- **data/** — Database schemas and data documentation
- **runtime/** — Configuration and operational documentation
- **testing/** — Testing strategies and procedures
- **compliance/** — Regulatory compliance documentation

### External Documentation (`external/`)
- **api/** — Public API documentation for consumers
- **integration/** — Integration guides and examples
- **sdk/** — SDK documentation and examples
- **guides/** — User guides and tutorials

## Documentation Standards

### Content Guidelines
- Use clear, professional English
- Follow standard technical writing conventions
- Use consistent terminology across all documents
- Include proper grammar and spelling
- Provide comprehensive explanations and context

### Formatting Standards
- Use standard markdown formatting
- Include proper headings hierarchy
- Use consistent file naming (kebab-case)
- Add cross-references between related documents
- Include table of contents for long documents

### Code Examples
- All code comments must be in English
- Use descriptive variable and function names
- Include explanatory text for complex examples
- Test all examples before publishing

## Getting Started

### For Developers
1. Start with `internal/README.md` for internal documentation structure
2. Review architecture documents in `internal/design/`
3. Check API specifications in `internal/api/`
4. Follow security guidelines in `internal/security/`

### For External Consumers
1. Start with `external/README.md` for external documentation
2. Review API documentation in `external/api/`
3. Check integration guides in `external/integration/`
4. Explore SDK examples in `external/sdk/`

## Contributing

When adding or updating documentation:

1. **Language Compliance**: Ensure all content is in English
2. **Structure**: Follow the established directory structure
3. **Formatting**: Use consistent markdown formatting
4. **Examples**: Include working code examples
5. **Testing**: Test all code samples before publishing
6. **Review**: Review for clarity, completeness, and accuracy

## Project Rules

This documentation follows the project rules defined in:
- `.cursorrules` — Main project rules
- `docs/.cursorrules` — Documentation-specific rules
- `docs/internal/.cursorrules` — Internal documentation rules
- `docs/external/.cursorrules` — External documentation rules

## Contact

For questions about documentation:
- Internal documentation: Contact the development team
- External documentation: Contact the API support team
