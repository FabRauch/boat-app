import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { openFile, byteSize } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './boat.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const BoatDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const boatEntity = useAppSelector(state => state.boat.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="boatDetailsHeading">Boat</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{boatEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{boatEntity.name}</dd>
          <dt>
            <span id="description">Description</span>
          </dt>
          <dd>{boatEntity.description}</dd>
          <dt>
            <span id="pic">Pic</span>
          </dt>
          <dd>
            {boatEntity.pic ? (
              <div>
                {boatEntity.picContentType ? (
                  <a onClick={openFile(boatEntity.picContentType, boatEntity.pic)}>
                    <img src={`data:${boatEntity.picContentType};base64,${boatEntity.pic}`} style={{ maxHeight: '30px' }} />
                  </a>
                ) : null}
                <span>
                  {boatEntity.picContentType}, {byteSize(boatEntity.pic)}
                </span>
              </div>
            ) : null}
          </dd>
        </dl>
        <Button tag={Link} to="/boat" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/boat/${boatEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default BoatDetail;
