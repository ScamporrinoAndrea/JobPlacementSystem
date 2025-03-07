import { Row, Col, InputGroup, Form, Button } from 'react-bootstrap'
import { Color } from '../constants/colors.ts'

const SearchBar = ({ search, handleSearch }: { search: string, handleSearch: (event: React.ChangeEvent<HTMLInputElement>) => void }) => {
    return (
        <Row style={{ paddingTop: 25 }}>
            <Col lg={{ span: 4, offset: 4 }} md={12}>
                <InputGroup>
                    <Form.Control
                        placeholder='Search'
                        style={{ borderTopLeftRadius: 50, borderBottomLeftRadius: 50, borderColor: Color.primary }}
                        value={search}
                        onChange={handleSearch}
                    />
                    <Button variant='outline-primary' style={{ borderTopRightRadius: 50, borderBottomRightRadius: 50 }}>
                        <i className='bi bi-search'></i>
                    </Button>
                </InputGroup>
            </Col>
        </Row>
    )
}

export default SearchBar