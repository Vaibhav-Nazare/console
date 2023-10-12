import { MenuToggle, Select, SelectOption } from "@/libs/patternfly/react-core";
import { useTranslations } from "next-intl";
import { useState } from "react";
import type { CheckboxType } from "../types";

export function FilterCheckbox({
  label,
  chips,
  options,
  onToggle,
}: Pick<CheckboxType<any>, "chips" | "options" | "onToggle"> & {
  label: string;
}) {
  const t = useTranslations();
  const [isOpen, setIsOpen] = useState(false);
  return (
    <Select
      aria-label={label}
      onSelect={(_, value) => {
        onToggle(value);
        setIsOpen(false);
      }}
      selected={chips}
      isOpen={isOpen}
      toggle={(toggleRef) => (
        <MenuToggle
          ref={toggleRef}
          onClick={() => setIsOpen((o) => !o)}
          isExpanded={isOpen}
          style={
            {
              width: "200px",
            } as React.CSSProperties
          }
        >
          {t("common.search_hint", { label })}
        </MenuToggle>
      )}
    >
      {Object.entries(options).map(([key, label]) => (
        <SelectOption
          key={key}
          value={key}
          hasCheckbox={true}
          isSelected={chips.includes(key)}
        >
          {label}
        </SelectOption>
      ))}
    </Select>
  );
}
