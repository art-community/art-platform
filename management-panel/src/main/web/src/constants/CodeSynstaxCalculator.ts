export const calculateCodeSyntax = (extension?: string) => {
    if (!extension) return undefined;
    if (extension == "yml") return "yaml";
    return extension;
};