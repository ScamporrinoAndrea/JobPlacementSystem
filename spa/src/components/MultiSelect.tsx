import React, { KeyboardEventHandler } from 'react';
import CreatableSelect from 'react-select/creatable';

const components = {
    DropdownIndicator: null,
};

export interface Option {
    readonly label: string;
    readonly value: string;
}

const createOption = (label: string): Option => ({
    label,
    value: label,
});

// Funzione per validare l'email con la regex
const isValidEmail = (email: string): boolean => {
    const emailRegex = /^[a-zA-Z0-9.]+@[a-zA-Z0-9.]+\.[a-zA-Z]{2,}$/;
    return emailRegex.test(email);
};

// Funzione per validare il numero di telefono con la regex
const isValidPhone = (phone: string): boolean => {
    const phoneRegex = /^\+?\d+$/;
    return phoneRegex.test(phone);
};

// Funzione per validare l'indirizzo con la regex
const isValidAddress = (address: string): boolean => {
    const addressRegex = /^[a-zA-Z\s]+,\s*[a-zA-Z0-9]+,\s[a-zA-Z\s]+,\s[a-zA-Z\s]+$/;
    return addressRegex.test(address);
};

export const EmailInput = ({ value, setValue }: { value: Option[], setValue: (value: Option[]) => void }) => {
    const [inputValue, setInputValue] = React.useState('');
    const [isInvalid, setIsInvalid] = React.useState(false);

    const handleKeyDown: KeyboardEventHandler = (event) => {
        if (!inputValue) return;
        switch (event.key) {
            case 'Enter':
            case 'Tab':
                if (isValidEmail(inputValue)) {
                    setValue([...value, createOption(inputValue)]);
                    setInputValue('');
                    setIsInvalid(false);
                } else {
                    setIsInvalid(true);
                }
                event.preventDefault();
                break;
        }
    };

    return (
        <>
            <CreatableSelect
                components={components}
                inputValue={inputValue}
                isClearable
                isMulti
                menuIsOpen={false}
                onChange={(newValue) => setValue(newValue as Option[])}
                onInputChange={(newValue) => {
                    setInputValue(newValue);
                    setIsInvalid(false);
                }}
                onKeyDown={handleKeyDown}
                placeholder="Enter emails and press enter"
                value={value}
                styles={{
                    control: (provided) => ({
                        ...provided,
                        borderColor: isInvalid ? 'red' : provided.borderColor,
                    }),
                }}
            />
            {isInvalid && (
                <div style={{
                    color: 'rgb(209,30,53,0.8)', marginTop: 5, fontSize: 14
                }}>
                    Invalid email format. Please enter a valid email.
                </div>
            )}
        </>
    );
};

export const PhoneInput = ({ value, setValue }: { value: Option[], setValue: (value: Option[]) => void }) => {
    const [inputValue, setInputValue] = React.useState('');
    const [isInvalid, setIsInvalid] = React.useState(false);

    const handleKeyDown: KeyboardEventHandler = (event) => {
        if (!inputValue) return;
        switch (event.key) {
            case 'Enter':
            case 'Tab':
                if (isValidPhone(inputValue)) {
                    setValue([...value, createOption(inputValue)]);
                    setInputValue('');
                    setIsInvalid(false);
                } else {
                    setIsInvalid(true);
                }
                event.preventDefault();
                break;
        }
    };

    return (
        <>
            <CreatableSelect
                components={components}
                inputValue={inputValue}
                isClearable
                isMulti
                menuIsOpen={false}
                onChange={(newValue) => setValue(newValue as Option[])}
                onInputChange={(newValue) => {
                    setInputValue(newValue);
                    setIsInvalid(false);
                }}
                onKeyDown={handleKeyDown}
                placeholder="Enter phone numbers and press enter"
                value={value}
                styles={{
                    control: (provided) => ({
                        ...provided,
                        borderColor: isInvalid ? 'red' : provided.borderColor,
                    }),
                }}
            />
            {isInvalid && (
                <div style={{
                    color: 'rgb(209,30,53,0.8)', marginTop: 5, fontSize: 14
                }}>
                    Invalid phone number format. Please enter a valid phone number.
                </div>
            )}
        </>
    );
};

export const AddressInput = ({ value, setValue }: { value: Option[], setValue: (value: Option[]) => void }) => {
    const [inputValue, setInputValue] = React.useState('');
    const [isInvalid, setIsInvalid] = React.useState(false);

    const handleKeyDown: KeyboardEventHandler = (event) => {
        if (!inputValue) return;
        switch (event.key) {
            case 'Enter':
            case 'Tab':
                if (isValidAddress(inputValue)) {
                    setValue([...value, createOption(inputValue)]);
                    setInputValue('');
                    setIsInvalid(false);
                } else {
                    setIsInvalid(true);
                }
                event.preventDefault();
                break;
        }
    };

    return (
        <>
            <CreatableSelect
                components={components}
                inputValue={inputValue}
                isClearable
                isMulti
                menuIsOpen={false}
                onChange={(newValue) => setValue(newValue as Option[])}
                onInputChange={(newValue) => {
                    setInputValue(newValue);
                    setIsInvalid(false);
                }}
                onKeyDown={handleKeyDown}
                placeholder="Enter addresses and press enter"
                value={value}
                styles={{
                    control: (provided) => ({
                        ...provided,
                        borderColor: isInvalid ? 'red' : provided.borderColor,
                    }),
                }}
            />
            {isInvalid && (
                <div style={{
                    color: 'rgb(209,30,53,0.8)', marginTop: 5, fontSize: 14
                }}>
                    Invalid address format. Please enter a valid address. Format: Street, Number, City, Country
                </div>
            )}
        </>
    );
};
