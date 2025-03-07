import { Image } from "react-bootstrap";
import { AddressInterface, EmailInterface, SpecificContactInterface, TelephoneInterface } from "../interfaces/interfaces"
import image from '../assets/default_image_profile.jpg';
import randomcolor from 'randomcolor';

export const LeftBarContactDetails = ({ contact }: { contact: SpecificContactInterface | null }) => {
    return (
        <>
            <div style={{ textAlign: 'center' }}>
                <Image src={image} rounded style={{ width: 150 }} />
            </div>
            <div style={{ textAlign: 'center', marginTop: 15 }}>
                <div style={{ fontWeight: 'medium', fontSize: 17 }}>{contact?.name + ' ' + contact?.surname}</div>
            </div>

            <div style={{ marginTop: 25 }}>
                <div style={{ fontWeight: 'medium', fontSize: 17 }}>Details</div>
                <hr />
            </div>
            <div>
                <span style={{ fontSize: 15, marginRight: 8 }}>Name:</span>
                <span style={{ fontSize: 14 }} className='text-muted'>
                    {contact?.name}
                </span>
            </div>
            <div>
                <span style={{ fontSize: 15, marginRight: 8 }}>Surname:</span>
                <span style={{ fontSize: 14 }} className='text-muted'>
                    {contact?.surname}
                </span>
            </div>
            {contact?.ssncode &&
                <div>
                    <span style={{ fontSize: 15, marginRight: 8 }}>SSN code:</span>
                    <span style={{ fontSize: 14 }} className='text-muted'>
                        {contact?.ssncode}
                    </span>
                </div>
            }
            {contact?.category &&
                <div>
                    <span style={{ fontSize: 15, marginRight: 8 }}>Category:</span>
                    <span style={{ fontSize: 14 }} className='text-muted'>
                        {contact?.category}
                    </span>
                </div>
            }
            {contact?.professionalInfo?.location ?
                <div>
                    <span style={{ fontSize: 15, marginRight: 8 }}>Location:</span>
                    <span style={{ fontSize: 14 }} className='text-muted'>
                        {contact?.professionalInfo?.location}
                    </span>
                </div> : null
            }
            {contact?.professionalInfo?.dailyRate ?
                <div>
                    <span style={{ fontSize: 15, marginRight: 8 }}>Daily rate:</span>
                    <span style={{ fontSize: 14 }} className='text-muted'>
                        {contact?.professionalInfo?.dailyRate} €
                    </span>
                </div> : null
            }
            {contact?.professionalInfo?.skills &&
                <div>
                    <div style={{ marginTop: 25 }}>
                        <div style={{ fontWeight: 'medium', fontSize: 17 }}>Skills</div>
                        <hr />
                    </div>
                    <div style={{ fontWeight: 'semi-bold', fontSize: 14, marginTop: 15 }}>
                        {contact?.professionalInfo?.skills.split(',').map((keyword) => (
                            <span
                                key={keyword}
                                className='badge'
                                style={{
                                    backgroundColor: randomcolor({ seed: keyword.replace(/\s+/g, '').toLowerCase(), luminosity: 'bright', format: 'rgba', alpha: 1 }).replace(/1(?=\))/, '0.1'),
                                    color: randomcolor({ seed: keyword.replace(/\s+/g, '').toLowerCase(), luminosity: 'bright', format: 'rgba', alpha: 1 }),
                                    padding: '0.5em 1.2em',
                                    borderRadius: '0.25rem',
                                    marginRight: 10,
                                    marginBottom: 10,
                                }}
                            >
                                {keyword}
                            </span>
                        ))}
                    </div>
                </div>
            }
            {contact && contact?.mailList.length > 0 ?
                <div>
                    <div style={{ marginTop: 25 }}>
                        <div style={{ fontWeight: 'medium', fontSize: 17 }}>Emails</div>
                        <hr />
                    </div>
                    {contact?.mailList.map((mail: EmailInterface, index: number) => {
                        return (
                            <div key={index} style={{ fontSize: 14 }} className='text-muted'>
                                {mail.mail}
                            </div>
                        )
                    })}
                </div> : null
            }
            {contact && contact?.addressList.length > 0 ?
                <div>
                    <div style={{ marginTop: 25 }}>
                        <div style={{ fontWeight: 'medium', fontSize: 17 }}>Address</div>
                        <hr />
                    </div>
                    {contact?.addressList.map((address: AddressInterface, index: number) => {
                        return (
                            <div key={index} style={{ fontSize: 14 }} className='text-muted'>
                                {address.address}
                            </div>
                        )
                    })}
                </div> : null
            }
            {contact && contact?.telephoneList.length > 0 ?
                <div>
                    <div style={{ marginTop: 25 }}>
                        <div style={{ fontWeight: 'medium', fontSize: 17 }}>Telephone</div>
                        <hr />
                    </div>
                    {contact?.telephoneList.map((telephone: TelephoneInterface, index: number) => {
                        return (
                            <div key={index} style={{ fontSize: 14 }} className='text-muted'>
                                {telephone.number}
                            </div>
                        )
                    })}
                </div> : null
            }

        </>
    )
}
