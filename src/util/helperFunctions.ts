export function getIndent(indent: number | undefined): string {
    const unit = "ch";
    return `${indent}${unit}`;
}

export function calculateMargin(value: number): number{
    return value && value > 0 ? (value * 8) : 0;
}